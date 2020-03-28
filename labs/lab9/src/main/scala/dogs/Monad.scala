package dogs

import scala.concurrent.{ExecutionContext, Future}

case class WrapperFuture[-I, +O](value: I => Future[O])

trait Functor[F[+_]] {
  def fmap[A, B](f: A => B, v: F[A]): F[B]
}

object Functor {
  def apply[F[+_]](implicit instance: Functor[F]): Functor[F] = instance

  implicit def functor[F[+_]](implicit M: Monad[F]): Functor[F] = M

  implicit def mapFunctor[K]: Functor[Map[K, +*]] = new Functor[Map[K, +*]] {
    override def fmap[A, B](f: A => B, v: Map[K, A]): Map[K, B] =
      v.map { case (k, a) => (k, f(a)) }
  }
}

trait Monad[F[+_]] extends Functor[F] {
  def pure[T](v: T): F[T]
  def flatMap[A, B](f: A => F[B], v: F[A]): F[B]
  def flatten[T](v: F[F[T]]): F[T] = flatMap(identity[F[T]], v)
  override def fmap[A, B](f: A => B, v: F[A]): F[B] = flatMap((i: A) => pure(f(i)), v)
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
  implicit def eitherMonad[E]: Monad[Either[E, +*]] = new Monad[Either[E, +*]] {
    override def pure[T](v: T): Either[E, T] = Right(v)
    override def flatMap[A, B](f: A => Either[E, B], v: Either[E, A]): Either[E, B] = v match {
      case Right(value) => f(value)
      case Left(l) => Left(l)
    }
  }
  implicit def vectorMonad: Monad[Vector] = new Monad[Vector] {
    override def pure[T](v: T): Vector[T] = Vector(v)
    override def flatMap[A, B](f: A => Vector[B], v: Vector[A]): Vector[B] = v.flatMap(f)
  }
  implicit def listMonad: Monad[List] = new Monad[List] {
    override def pure[T](v: T): List[T] = List(v)
    override def flatMap[A, B](f: A => List[B], v: List[A]): List[B] = v.flatMap(f)
  }
  implicit def function1Monad[E]: Monad[E => +*] = new Monad[E => +*] {
    override def pure[T](v: T): E => T = (_: E) => v
    override def flatMap[A, B](f: A => E => B, v: E => A): E => B = (e: E) => f(v(e))(e)
  }
  implicit def futureMonad(implicit ec: ExecutionContext): Monad[Future] = new Monad[Future] {
    override def pure[T](v: T): Future[T] = Future.successful(v)
    override def flatMap[A, B](f: A => Future[B], v: Future[A]): Future[B] = v.flatMap(f)
  }
  implicit val idMonad: Monad[Id] = new Monad[Id] {
    override def pure[T](v: T): Id[T] = v
    override def flatMap[A, B](f: A => Id[B], v: Id[A]): Id[B] = f(v)
  }
  implicit def wrapperFutureMonad[I](implicit ec: ExecutionContext): Monad[WrapperFuture[I, +*]] =
    new Monad[WrapperFuture[I, +*]] {
      override def pure[T](v: T): WrapperFuture[I, T] = WrapperFuture(_ => Future.successful(v))
      override def flatMap[A, B](f: A => WrapperFuture[I, B], v: WrapperFuture[I, A]): WrapperFuture[I, B] =
        WrapperFuture(i => v.value(i).flatMap(x => f(x).value(i)))
    }
}