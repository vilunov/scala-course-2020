package lab7

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

abstract class PartialOrdProperties[T: Arbitrary](name: String) extends PartialEqualityProperties(name) {
  def instance: PartialOrd[T]

  property("partial ordering is reflexive") = forAll { a: T =>
    instance.partialCompare(a, a) match {
      case Some(OrdResult.Greater) => false
      case None => false
      case _ => true
    }
  }

  property("partial ordering is antisymmetric") = forAll { (left: T, right: T) =>
    instance.partialCompare(left, right) match {
      case Some(OrdResult.Equal) =>
        val a = instance.partialCompare(right, left)
        !a.contains(OrdResult.Greater) && !a.contains(OrdResult.Less)
      case _ => true
    }
  }

  property("partial ordering is transitive") = forAll { (a: T, b: T, c: T) =>
    instance.partialCompare(a, b) match {
      case None => true
      case ab => // Some(Any)
        instance.partialCompare(b, c) match {
          case None => true
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
