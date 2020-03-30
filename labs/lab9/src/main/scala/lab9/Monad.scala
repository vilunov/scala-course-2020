package lab9

trait Monad[F[+_]] extends Functor[F] {
  def pure[T](v: T): F[T]
  def flatMap[A, B](f: A => F[B], v: F[A]): F[B]
  def flatten[T](v: F[F[T]]): F[T] = flatMap(identity[F[T]], v)

  override def map[A, B](fa: F[A])(f: A => B): F[B] = flatMap((i: A) => pure(f(i)), fa)
}

object Monad {
  def apply[F[+_]](implicit monad: Monad[F]): Monad[F] = monad
}