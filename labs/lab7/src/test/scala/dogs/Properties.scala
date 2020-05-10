package dogs

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

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

//abstract class ListOrdProperties[T: Arbitrary](name: String) extends Properties(name) {
//  def instance: Ord[List[T]]
//
//  property("lists always sorted correctly") = forAll{ (l, r) =>
////    instance.
//  }
//}

abstract class MonoidProperties[T: Arbitrary](name: String) extends Properties(name) {
  def instance: Monoid[T]

  property("unit with object equal to object") = forAll { (item: T) =>
    instance.combine(instance.unit, item) == item
  }

  property("left comb right is right comb left") = forAll { (left: T, right: T) =>
    instance.combine(left, right) == instance.combine(right, left)
  }

  property("((a, b), c) == (a, (b, c))") = forAll { (a: T, b: T, c: T) =>
    instance.combine(instance.combine(a, b), c) == instance.combine(a, instance.combine(b, c))
  }
}
