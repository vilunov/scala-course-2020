package lab8.syntax

import lab8.{Monad, Functor}

package object monad {

  implicit class SyntaxMonad[F[+ _], T](val value: F[T]) extends AnyVal {
    implicit def flatMap[B](f: T => F[B])(implicit M: Monad[F]): F[B] =
      M.flatMap(f)(value)

    implicit def map[B](f: T => B)(implicit M: Monad[F]): F[B]=
      M.fmap(f)(value)
  }

  implicit class SyntaxNestedMonad[F[+ _], T](val value: F[F[T]]) extends AnyVal {

    implicit def flattenMonad(implicit M: Monad[F]): F[T] =
      M.flatten(value)
  }

  implicit class SyntaxPureMonad[T](val value: T) extends AnyVal {
    implicit def pureMonad[F[+ _]](implicit M: Monad[F]): F[T] =
      M.pure(value)
  }
}

package object functor {

  implicit class SyntaxFunctor[F[+ _], T](val value: F[T]) extends AnyVal {

    implicit def fmapFunctor[B](f: T => B)(implicit F: Functor[F]): F[B] =
      F.fmap(f)(value)
  }

}
