package lab9

import scala.concurrent.{ExecutionContext, Future}

case class FutureWrapper[-I, +O](param: I => Future[O])

trait Monad[F[+_]] extends Functor[F] {
  def pure[T](v: T): F[T]
  def flatMap[A, B](f: A => F[B], v: F[A]): F[B]
  def flatten[T](v: F[F[T]]): F[T] = flatMap(identity[F[T]], v)

  override def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap((i: A) => pure(f(i)), fa)
}

object Monad {
  def apply[F[+_]](implicit monad: Monad[F]): Monad[F] = monad

  implicit def optionMonad: Monad[Option] = new Monad[Option] {
    override def pure[T](v: T): Option[T] = Some(v)

    override def flatMap[A, B](f: A => Option[B], v: Option[A]): Option[B] = v.flatMap(f)
  }

  implicit def eitherMonad[K]: Monad[Either[K, +*]] = new Monad[Either[K, +*]] {
    override def pure[T](v: T): Either[K, T] = Right(v)

    override def flatMap[A, B](f: A => Either[K, B], v: Either[K, A]): Either[K, B] = v match {
      case Left(v) => Left(v)
      case Right(v) => f(v)
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

  implicit def unaryFunctionMonad[K]: Monad[K => +*] = new Monad[Function[K, +*]] {
    override def pure[T](v: T): Function[K, T] = {
      _: K => v
    }

    override def flatMap[A, B](f: A => Function[K, B], v: Function[K, A]): Function[K, B] = {
      param: K => f(v(param))(param)
    }
  }

  implicit def futureMonad(implicit context: ExecutionContext): Monad[Future] = new Monad[Future] {
    override def pure[T](v: T): Future[T] = Future(v)

    override def flatMap[A, B](f: A => Future[B], v: Future[A]): Future[B] = v.flatMap(f)
  }

  implicit def futureMonadWrapper[I](implicit context: ExecutionContext): Monad[FutureWrapper[I, +*]] = new Monad[FutureWrapper[I, +*]] {
    override def pure[T](v: T): FutureWrapper[I, T] = FutureWrapper(_ => Future.successful(v))

    override def flatMap[A, B](f: A => FutureWrapper[I, B], v: FutureWrapper[I, A]): FutureWrapper[I, B] =
      FutureWrapper(a => v.param(a).flatMap(b => f(b).param(a)))
  }
}
