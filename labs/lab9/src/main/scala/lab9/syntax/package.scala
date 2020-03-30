package lab9

import lab9.{Functor, Monad}

package object syntax {
  implicit class FunctorMonadSyntax[F[+_], T](val v: F[T]) extends AnyVal {
    def map[K](f: T => K)(implicit F: Functor[F]): F[K] = F.map(v)(f)

    def flatMap[K](f: T => F[K])(implicit M: Monad[F]): F[K] = M.flatMap(f, v)
  }
}
