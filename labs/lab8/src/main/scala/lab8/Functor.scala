package lab8

trait Functor[F[+ _]] {
  /**
   * There are two laws to monad instances:
   * *
   * • Lifted identity function should be identity:
   * ∀ a:F[T] . fmap(identity)(a) == a
   * *
   * • Lifted composition of two functions should behave the same as composition of two lifted functions:
   * ∀ a:F[T], f: T => A, g: A => B
   * fmap { (x: T) => g(f(x)) } (a) == fmap(g)(fmap(f)(a))
   **/
  def fmap[A, B](f: A => B)(v: F[A]): F[B]
}

object Functor {
  def apply[F[+ _]](implicit instance: Functor[F]): Functor[F] = instance

  implicit def monadToFunctor[F[+_]](M: Monad[F]): Functor[F] = M

  implicit def mapFunctor[K]: Functor[Map[K, +*]] = new Functor[Map[K, +*]] {
    override def fmap[A, B](f: A => B)(v: Map[K, A]): Map[K, B] =
      v.view.mapValues(f).toMap
  }
}
