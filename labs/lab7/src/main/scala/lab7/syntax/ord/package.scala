package lab7.syntax

import lab7.{OrdResult, PartialEquality, PartialOrd}

package object ord {

  implicit class SyntaxPartialEquality[T](val left: T) extends AnyVal {
    def is(right: T)(implicit E: PartialEquality[T]): Boolean = E.equal(left, right)
  }

  implicit class SyntaxPartialOrder[T](val left: T) extends AnyVal {
    def <(right: T)(implicit O: PartialOrd[T]): Boolean =
      O.partialCompare(left, right).fold(false)(_ == OrdResult.Less)

    def >(right: T)(implicit O: PartialOrd[T]): Boolean =
      O.partialCompare(left, right).fold(false)(_ == OrdResult.Greater)

    def <=(right: T)(implicit O: PartialOrd[T]): Boolean =
      O.partialCompare(left, right).fold(false)(i => i == OrdResult.Less || i == OrdResult.Equal)

    def >=(right: T)(implicit O: PartialOrd[T]): Boolean =
      O.partialCompare(left, right).fold(false)(i => i == OrdResult.Greater || i == OrdResult.Equal)
  }

}
