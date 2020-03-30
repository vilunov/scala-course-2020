package lab9

trait Functor[F[+_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object Functor {
  def apply[F[+_]](implicit functor: Functor[F]): Functor[F] = functor
}
