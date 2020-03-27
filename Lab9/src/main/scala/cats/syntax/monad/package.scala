package cats.syntax

import cats.{Functor, Monad}

package object monad {

  implicit class SyntaxMonad[F[+_], T](val value: F[T]) extends AnyVal {
    def flatMap[O](f: T => F[O])(implicit M: Monad[F[T]]): F[O] = M.flatMap(f, value)
  }

  implicit class SyntaxFunctor[F[+_], T](val value: F[T]) extends AnyVal {
    def map[O](f: T => O)(implicit F: Functor[F[T]]): F[O] = F.fmap(f, value)
  }

}
