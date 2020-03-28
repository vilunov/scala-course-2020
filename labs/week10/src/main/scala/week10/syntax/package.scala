package week10

package object syntaxFunctor {

  implicit class SyntaxFunctor[F[+_], T](val value: F[T]) extends AnyVal {
    implicit def fmap[A](f: T => A)(implicit F: Functor[F]): F[A] =
      F.fmap(f, value)

    implicit def map[A](f: T => A)(implicit M: Functor[F]): F[A] = fmap(f)

    implicit def foreach[A](f: T => Unit)(implicit M: Functor[F]): Unit = fmap(f)
  }
}

package object syntaxMonad {
  implicit class SyntaxMonad [M[+ _], T](val value: M[T]) extends AnyVal {
    implicit def flatMap[A](f: T => M[A])(implicit M: Monad[M]): M[A] =
      M.flatMap(f, value)
  }
}
