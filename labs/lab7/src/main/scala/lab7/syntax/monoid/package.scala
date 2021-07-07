package lab7.syntax

import lab7.{Monoid, Semigroup}

package object monoid {

  implicit class SyntaxSemigroupMonoid[T](val left: T) extends AnyVal {
    def |+|(right: T)(implicit S: Semigroup[T]): T = S.combine(left, right)
  }

  implicit class SyntaxIterable[T](val inner: Iterable[T]) extends AnyVal {
    def reduceMonoid(implicit M: Monoid[T]): T = inner.fold(M.neutral)(M.combine)

    def foldSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.fold(start)(S.combine)

    def foldLeftSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.foldLeft(start)(S.combine)

    def foldRightSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.foldRight(start)(S.combine)
  }

}
