package dogs

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}
import dogs.syntax.ord._
import dogs.syntax.monoid._

abstract class EqualityProperties[T: Arbitrary](name: String) extends PartialEqualityProperties[T](name) {
  implicit def instance: Equality[T]

  property("total equality is reflexive") = forAll { a: T =>
    a is a
  }
}

abstract class PartialEqualityProperties[T: Arbitrary](name: String) extends Properties(name) {
  implicit def instance: PartialEquality[T]

  property("partial equality is symmetric") = forAll { (left: T, right: T) =>
    (left is right) == (right is left)
  }

  property("partial equality is transitive") = forAll { (a: T, b: T, c: T) =>
    !((a is b) && (b is c)) || (a is c)
  }
}

abstract class OrdProperties[T: Arbitrary](name: String) extends PartialOrdProperties[T](name) {
  implicit def instance: Ord[T]

  property("total order is reflexive") = forAll { a: T =>
    a is a
  }
}

abstract class PartialOrdProperties[T: Arbitrary](name: String) extends Properties(name) {
  implicit def instance: PartialOrd[T]

  property("partial order is antisymmetric") = forAll { (left: T, right: T) =>
    !((left <= right) && (right <= left)) || (left is right)
  }

  property("partial order is transitive") = forAll { (a: T, b: T, c: T) =>
    !((a <= b) && (b <= c)) || (a <= c)
  }
}

abstract class SemigroupProperties[T: Arbitrary](name: String) extends Properties(name) {
  implicit def instance: Semigroup[T]

  property("semigroup is associative") = forAll { (a: T, b: T, c: T) =>
    ((a |+| b) |+| c) == (a |+| (b |+| c))
  }
}

abstract class CommutativeSemigroupProperties[T: Arbitrary](name: String) extends SemigroupProperties(name) {
  implicit def instance: CommutativeSemigroup[T]

  property("commutative semigroup is commutative") = forAll { (a: T, b: T) =>
    (a |+| b) == (b |+| a)
  }
}

abstract class MonoidProperties[T: Arbitrary](name: String) extends SemigroupProperties(name) {
  implicit def instance: Monoid[T]

  property("monoid has unit") = forAll { a: T =>
    (instance.unit |+| a) == a && a == (a |+| instance.unit)
  }
}

abstract class CommutativeMonoidProperties[T: Arbitrary](name: String) extends MonoidProperties(name) {
  implicit def instance: CommutativeMonoid[T]

  property("commutative monoid is commutative") = forAll { (a: T, b: T) =>
    (a |+| b) == (b |+| a)
  }
}