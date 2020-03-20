package lab7

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}
import lab7.syntax.ord.SyntaxPartialOrder
import lab7.syntax.ord.SyntaxPartialEquality

abstract class EqualityProperties[T: Arbitrary](name: String) extends PartialEqualityProperties[T](name) {
  def instance: Equality[T]

  property("total equality is reflexive") = forAll { a: T =>
    instance.equal(a, a)
  }
}

abstract class PartialEqualityProperties[T: Arbitrary](name: String) extends Properties(name) {
  def instance: PartialEquality[T]

  property("partial equality is symmetric") = forAll { (left: T, right: T) =>
    instance.equal(left, right) == instance.equal(right, left)
  }

  property("partial equality is transitive") = forAll { (a: T, b: T, c: T) =>
    !(instance.equal(a, b) && instance.equal(b, c)) || instance.equal(a, c)
  }
}

abstract class PartialOrdProperties[T: Arbitrary](name: String) extends PartialEqualityProperties(name) {
  implicit def instance: PartialOrd[T]

  property("partial ordering is reflexive") = forAll { a: T =>
    a >= a
  }

  property("partial ordering is antisymmetric") = forAll { (left: T, right: T) =>
    if (left >= right && right >= left) left is right else true
  }

  property("partial ordering is transitive") = forAll { (a: T, b: T, c: T) =>
    instance.partialCompare(a, b) match {
      case ab =>
        instance.partialCompare(b, c) match {
          case bc if ab == bc =>
            instance.partialCompare(a, c) == ab
          case _ => true
        }
    }
  }
}

abstract class OrdProperties[T: Arbitrary](name: String) extends PartialOrdProperties(name) {
  def instance: Ord[T]

  property("total ordering is reflexive") = forAll { a: T =>
    instance.equal(a, a)
  }
}
