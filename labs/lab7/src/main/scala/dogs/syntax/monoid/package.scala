package dogs.syntax

import dogs.{Monoid, Semigroup}

package object monoid {

  implicit class SyntaxSemigroupMonoid[T](val left: T) extends AnyVal {
    def |+|(right: T)(implicit S: Semigroup[T]): T = ???
  }

  implicit class SyntaxIterable[T](val inner: Iterable[T]) extends AnyVal {
    def reduceMonoid(implicit M: Monoid[T]): T = ???

    def foldSemigroup(start: T)(implicit S: Semigroup[T]): T = ???

    def foldLeftSemigroup(start: T)(implicit S: Semigroup[T]): T = ???

    def foldRightSemigroup(start: T)(implicit S: Semigroup[T]): T = ???
  }

}
