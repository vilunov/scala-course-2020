package dogs

import dogs.syntax.ord._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

abstract class EqualityProperties[T: Arbitrary](name: String)
    extends PartialEqualityProperties[T](name) {
  def instance: Equality[T]

  property("total equality is reflexive") = forAll { a: T =>
    instance.equal(a, a)
  }
}

abstract class PartialEqualityProperties[T: Arbitrary](name: String)
    extends Properties(name) {
  def instance: PartialEquality[T]

  property("partial equality is symmetric") = forAll { (left: T, right: T) =>
    instance.equal(left, right) == instance.equal(right, left)
  }

  property("partial equality is transitive") = forAll { (a: T, b: T, c: T) =>
    !(instance.equal(a, b) && instance.equal(b, c)) || instance.equal(a, c)
  }
}

abstract class PartialOrdProperties[T: Arbitrary](name: String)
    extends PartialEqualityProperties[T](name) {
  implicit def instance: PartialOrd[T]

  property("partial order is reflexive") = forAll { a: T =>
    a <= a
  }

  property("partial order is antisymmetric") = forAll { (a: T, b: T) =>
    !(a <= b && b <= a) || a == b
  }

  property("partial order is transitive") = forAll { (a: T, b: T, c: T) =>
    !(a <= b && b <= c) || a <= c
  }
}

abstract class PartialOrdWithEqualityProperties[T: Arbitrary](name: String)
    extends PartialOrdProperties[T](name) {
  implicit def instance: PartialOrd[T]

  property("total order's equality is reflexive") = forAll { a: T =>
    a == a
  }
}

abstract class OrdProperties[T: Arbitrary](name: String)
    extends PartialOrdWithEqualityProperties[T](name) {
  def instance: Ord[T]

  property("all elements are comparable") = forAll { (a: T, b: T) =>
    instance.partialCompare(a, b).isDefined
  }
}
