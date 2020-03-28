package dogs.syntax

import dogs.{Functor, Monad}

package object monad {

  implicit class SyntaxFunctor[F[+_], T](private val value: F[T]) extends AnyVal {
    implicit def fmap[B](f: T => B)(implicit M: Functor[F]): F[B] = M.fmap(f, value)
    implicit def map[B](f: T => B)(implicit M: Functor[F]): F[B] = fmap(f)
    implicit def foreach[B](f: T => Unit)(implicit M: Functor[F]): Unit = fmap(f)
  }

  implicit class SyntaxMonad[F[+_], T](private val value: F[T]) extends AnyVal {
    implicit def flatMap[B](f: T => F[B])(implicit M: Monad[F]): F[B] = M.flatMap(f, value)
  }
}
