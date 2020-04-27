package week10

import week10.syntaxFunctor._
import week10.syntaxMonad._
import scala.concurrent.{ExecutionContext, Future}

trait Functor[F[+_]] {
  def fmap[A, B](f: A => B, v: F[A]): F[B]
}

// This id definition from slides
trait Monad[F[+_]] extends Functor[F] {
  def pure[T](v: T): F[T]

  def flatMap[A, B](f: A => F[B], v: F[A]): F[B]

  def flatten[T](v: F[F[T]]): F[T] = flatMap(identity[F[T]], v)

  override def fmap[A, B](f: A => B, v: F[A]): F[B] = flatMap((i: A) => pure(f(i)), v)
}

//wrapper for future
case class FutureWrapper[-I, +O](value: I => Future[O])

object Functor {

  type Function[-I, +O] = I => O

  def apply[F[+_]](implicit instance: Functor[F]): Functor[F] = instance

  implicit def functor[F[+_]](implicit M: Monad[F]): Functor[F] = M

  implicit val functorForOption: Functor[Option] = new Functor[Option] {
    override def fmap[A, B](f: A => B, v: Option[A]): Option[B] = v match {
      case Some(value) => Some(f(value))
      case None => None
    }
  }

  implicit def functorForEither[E]: Functor[Either[E, +*]] = new Functor[Either[E, +*]] {
    override def fmap[A, B](f: A => B, v: Either[E, A]): Either[E, B] = v match {
      case Right(value) => Right(f(value))
      case Left(value) => Left(value)
    }
  }

  implicit val functorForVector: Functor[Vector] = new Functor[Vector] {
    override def fmap[A, B](f: A => B, v: Vector[A]): Vector[B] = v.map(i => f(i))
  }
  implicit val functorForList: Functor[List] = new Functor[List] {
    override def fmap[A, B](f: A => B, v: List[A]): List[B] = v.map(i => f(i))
  }

  implicit def functorForFuture (implicit ec: ExecutionContext) : Functor[Future] = new Functor[Future] {
    override def fmap[A, B](f: A => B, v: Future[A]): Future[B] = v.map(i => f(i))
  }

  implicit def functorForUnary[I] : Functor[Function[I, +*]] = new Functor[Function[I, +*]] {
    override def fmap[A, B](f: A => B, v: Function[I, A]): Function[I,B] = (arg:I) => f(v(arg))
  }

  implicit def functorforMap[C]: Functor[Map[C, +*]] = new Functor[Map[C, +*]]{
    override def fmap [A,B](f: A => B, v: Map[C, A]): Map[C,B] = v.map({case (c, a)=>(c, f(a))})
  }

  implicit def functorforId :Functor[Id] =new Functor[Id] {
    override def fmap[A, B](f: A => Id[B], v: Id[A]): Id[B] = f(v)
  }
}

// Map in second type parameter: [K]Map[K, *] – impossible to define pure
object Monad {

  type Function[-I, +O] = I => O

  def apply[F[+_]](implicit instance: Monad[F]): Monad[F] = instance

  implicit val monadForOption: Monad[Option] = new Monad[Option] {
    override def pure[T](v: T): Option[T] = Some(v)

    override def flatMap[A, B](f: A => Option[B], v: Option[A]): Option[B] = v match {
      case Some(value) => f(value)
      case None => None
    }
  }

  implicit val monadForList: Monad[List] = new Monad[List] {
    override def pure[T](v: T): List[T] = List(v)

    override def flatMap [A, B](f: A => List[B], v: List[A]): List[B] = v.flatMap(f)
  }

  implicit val monadForVector: Monad[Vector] = new Monad[Vector] {
    override def pure[T](v: T): Vector[T] = Vector(v)

    override def flatMap [A, B](f: A => Vector[B], v: Vector[A]): Vector[B] = v.flatMap(f)
  }

  implicit def monadForFuture(implicit ec: ExecutionContext): Monad[Future] = new Monad[Future] {
    override def pure[T](v: T): Future[T] = Future.successful(v)

    override def flatMap[A, B](f: A => Future[B], v: Future[A]): Future[B] = v.flatMap(f)
  }

  implicit val monadForId: Monad[Id] = new Monad[Id] {
    override def pure[T](v: T):Id[T]=v

    override def flatMap[A, B](f: A => Id[B], v: Id[A]): Id[B] = f(v)
  }

  implicit def monadForEither[E]: Monad[Either[E, +*]] = new Monad[Either[E, +*]] {
    override def pure[T](v: T):Either[E, T]= Right(v)

    override def flatMap[A, B](f: A => Either[E, B], v: Either[E,A]): Either[E, B] = v.flatMap(f)
  }

  implicit def monadForUnary[I]: Monad[Function[I, +*]] = new Monad[Function[I, +*]] {
    override def pure[T](v: T):Function[I, T]= _ => v

    override def flatMap[A, B](f: A => Function[I, B], v: Function[I,A]): Function[I, B] =  (arg:I) => f(v(arg))(arg)
  }

  implicit def monadForWraper[I](implicit ec: ExecutionContext): Monad[FutureWrapper[I, +*]] =
    new Monad[FutureWrapper[I, +*]] {
      override def pure[T](v: T):FutureWrapper[I, T] = FutureWrapper(_ => Future.successful(v))

      override def flatMap[A, B](f: A => FutureWrapper[I, B], v: FutureWrapper[I,A]): FutureWrapper[I, B] =
        FutureWrapper(a => v.value(a).flatMap(x => f(x).value(a)))
    }
//
  def sequence[F[+_]: Monad, T](v: List[F[T]]): F[List[T]] = {
    var output =List.empty[T]
    for (i <- v){ i.map(t => {
      output = output:+t
    })
    }
    Monad[F].pure(output)
  }

}

