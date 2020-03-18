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

abstract class OrdProperties[T: Arbitrary](name: String) extends PartialOrdProperties[T](name) {
  def instance: Ord[T]

  property("total equality is reflexive") = forAll { a: T =>
    instance.equal(a, a)
  }

  property("total ordering is reflexive") = forAll { a: T =>
    instance.compare(a, a) != OrdResult.Greater
  }

  property("total ordering is antisymmetric") = forAll { (a: T, b: T) =>
    val abLessEq = instance.compare(a, b) != OrdResult.Greater
    val baLessEq = instance.compare(b, a) != OrdResult.Greater
    val abEq = instance.equal(a, b)
    !(abLessEq && baLessEq) || abEq
  }

  property("total ordering is transitive") = forAll { (a: T, b: T, c: T) =>
    val abLessEq = instance.compare(a, b) != OrdResult.Greater
    val bcLessEq = instance.compare(b, c) != OrdResult.Greater
    val acLessEq = instance.compare(a, c) != OrdResult.Greater
    !(abLessEq && bcLessEq) || acLessEq
  }

}

abstract class PartialOrdProperties[T: Arbitrary](name: String) extends PartialEqualityProperties[T](name) {
  def instance: PartialOrd[T]

  property("partial order is relaxed reflexive") = forAll { a: T =>
    instance.partialCompare(a, a).fold(true)(_ != OrdResult.Greater)
  }

  property("partial order is relaxed antisymmetric") = forAll { (a: T, b: T) =>
    val abLessEq = instance.partialCompare(a, b).fold(true)(_ != OrdResult.Greater)
    val baLessEq = instance.partialCompare(b, a).fold(true)(_ != OrdResult.Greater)
    val abEq = instance.partialCompare(a, b).fold(false)(_ == OrdResult.Equal)
    !(abLessEq && baLessEq) || abEq
  }

  property("partial order is relaxed transitive") = forAll { (a: T, b: T, c: T) =>
    val abLessEq = instance.partialCompare(a, b).fold(true)(_ != OrdResult.Greater)
    val bcLessEq = instance.partialCompare(b, c).fold(true)(_ != OrdResult.Greater)
    val acLessEq = instance.partialCompare(a, c).fold(false)(_ != OrdResult.Greater)
    !(abLessEq && bcLessEq) || acLessEq
  }

}
