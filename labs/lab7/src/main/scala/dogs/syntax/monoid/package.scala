package dogs.syntax

import dogs.{Monoid, Semigroup}

package object monoid {

  implicit class SyntaxSemigroupMonoid[T](private val left: T) extends AnyVal {
    def |+|(right: T)(implicit S: Semigroup[T]): T = S.combine(left, right)
  }

  implicit class SyntaxIterable[T](private val inner: Iterable[T]) extends AnyVal {
    def reduceMonoid(implicit M: Monoid[T]): T = inner.reduceOption(M.combine).getOrElse(M.unit)

    def foldSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.fold(start)(S.combine)

    def foldLeftSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.foldLeft(start)(S.combine)

    def foldRightSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.foldRight(start)(S.combine)
  }

}
