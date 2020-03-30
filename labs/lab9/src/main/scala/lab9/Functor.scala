package lab9

trait Functor[F[+_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
  def lift[A, B](f: A => B): F[A] => F[B] = ???
}

object Functor {
  def apply[F[+_]](implicit functor: Functor[F]): Functor[F] = functor

  implicit def functorFromMonad[F[+_]](implicit M: Monad[F]): Functor[F] = M

  implicit def mapFunctor[K]: Functor[Map[K, +*]] = new Functor[Map[K, +*]]{
    override def map[A, B](fa: Map[K, A])(f: A => B): Map[K, B] = {
      fa.map {
        case (key, value) => (key, f(value))
      }
    }
  }
}
