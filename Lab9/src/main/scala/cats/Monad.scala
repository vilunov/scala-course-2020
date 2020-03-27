package cats

import scala.concurrent.{ExecutionContext, Future}

trait Functor[F[+_]] {

  def fmap[A, B](f: A => B, v: F[A]): F[B]
}

object Functor {

  def apply[F[+_]](implicit instance: Functor[F]): Functor[F] = instance

  implicit def functorFromMonad[F[+_]](implicit monad: Monad[F]): Functor[F] = monad

  implicit def mapFunctor[K]: Functor[Map[K, +*]] = new Functor[Map[K, +*]] {
    override def fmap[A, B](f: A => B, v: Map[K, A]): Map[K, B] = v.map { case (key, value) => (key, f(value)) }
  }
}

trait Monad[F[+_]] extends Functor[F] {

  def pure[T](v: T): F[T]

  def flatMap[A, B](f: A => F[B], v: F[A]): F[B]

  def flatten[T](v: F[F[T]]): F[T] = flatMap(identity[F[T]], v)

  override def fmap[A, B](f: A => B, v: F[A]): F[B] = flatMap((x: A) => pure(f(x)), v)
}

object Monad {

  def apply[F[+_]](implicit instance: Monad[F]): Monad[F] = instance

  implicit val optionMonad: Monad[Option] = new Monad[Option] {
    override def pure[T](v: T): Option[T] = Some(v)

    override def flatMap[A, B](f: A => Option[B], v: Option[A]): Option[B] = v match {
      case Some(value) => f(value)
      case None => None
    }
  }

  implicit def eitherMonad[L]: Monad[Either[L, *]] = new Monad[Either[L, *]] {
    override def pure[T](v: T): Either[L, T] = Right(v)

    override def flatMap[A, B](f: A => Either[L, B], v: Either[L, A]): Either[L, B] = v match {
      case Right(value) => f(value)
      case Left(value) => Left(value)
    }
  }

  implicit val vectorMonad: Monad[Vector[*]] = new Monad[Vector] {
    override def pure[T](v: T): Vector[T] = Vector(v)

    override def flatMap[A, B](f: A => Vector[B], v: Vector[A]): Vector[B] = v.flatMap(f)
  }

  implicit val listMonad: Monad[List[*]] = new Monad[List] {
    override def pure[T](v: T): List[T] = List(v)

    override def flatMap[A, B](f: A => List[B], v: List[A]): List[B] = v.flatMap(f)
  }

  type Function[-I, +O] = I => O

  implicit def unaryMonad[I]: Monad[I => +*] = new Monad[Function[I, +*]] {
    override def pure[T](v: T): Function[I, T] = _ => v

    override def flatMap[A, B](f: A => Function[I, B], v: Function[I, A]): Function[I, B] =
      arg => f(v(arg))(arg)
  }

  implicit def futureMonad(implicit ec: ExecutionContext): Monad[Future[*]] = new Monad[Future] {
    override def pure[T](v: T): Future[T] = Future.successful(v)

    override def flatMap[A, B](f: A => Future[B], v: Future[A]): Future[B] = v.flatMap(f)
  }

  type Id[+X] = X

  implicit val idMonad: Monad[Id] = new Monad[Id] {
    override def pure[T](v: T): Id[T] = v

    override def flatMap[A, B](f: A => Id[B], v: Id[A]): Id[B] = f(v)
  }

  implicit def wrapMonad[I](implicit ec: ExecutionContext): Monad[Wrap[I, *]] = new Monad[Wrap[I, *]] {
    override def pure[T](v: T): Wrap[I, T] = Wrap(_ => Future.successful(v))

    override def flatMap[A, B](f: A => Wrap[I, B], v: Wrap[I, A]): Wrap[I, B] =
      Wrap(arg => v.value(arg).flatMap(x => f(x).value(arg)))
  }
}