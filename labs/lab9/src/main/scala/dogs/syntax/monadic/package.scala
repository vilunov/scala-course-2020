package dogs.syntax

import dogs.{Functor, Monad}

package object monadic {

  implicit final class MonadSyntax[F[+_], T](private val inner: F[T]) extends AnyVal {
    def flatMap[O](f: T => F[O])(implicit M: Monad[F]): F[O] = M.flatMap(f, inner)

    def map[O](f: T => O)(implicit F: Functor[F]): F[O] = F.fmap(f, inner)
  }

  implicit final class PureSyntax[T](private val inner: T) extends AnyVal {
    def pure[F[+_]](implicit M: Monad[F]): F[T] = M.pure(inner)
  }

}
