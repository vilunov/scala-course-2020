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

abstract class OrdProperties[T: Arbitrary](name: String) extends Properties(name) {
  def instance: Ord[T]

  property("total order is reflexive") = forAll { a: T =>
      instance.compare(a, a) != OrdResult.Greater
    }

  property("total order is antisymmetric") = forAll { (a: T, b: T) =>
      val lessOrEqual = instance.compare(a, b) != OrdResult.Greater &&
        instance.compare(b, a) != OrdResult.Greater
      val equal = instance.equal(a, b)

      !lessOrEqual || equal
    }

  property("total order is transitive") = forAll { (a: T, b: T, c: T) =>
      val lessOrEqual = instance.compare(a, b) != OrdResult.Greater &&
        instance.compare(b, c) != OrdResult.Greater

      val notGreater = instance.compare(a, c) != OrdResult.Greater

      !lessOrEqual || notGreater
  }
}

abstract class PartialOrdProperties[T: Arbitrary](name: String) extends Properties(name) {
  def instance: PartialOrd[T]

  property("total order is reflexive") = forAll { a: T =>
    instance.partialCompare(a, a) match {
      case Some(res) => res != OrdResult.Greater
      case _ => true
    }
  }

  property("total order is antisymmetric") = forAll { (a: T, b: T) =>
    val aLessOrEqualB = instance.partialCompare(a, b) match {
      case Some(res) => res != OrdResult.Greater
      case _ => true
    }

    val bLessOrEqualA = instance.partialCompare(b, a) match {
      case Some(res) => res != OrdResult.Greater
      case _ => true
    }

    val equal = instance.partialCompare(a, b) match {
      case Some(res) => res == OrdResult.Equal
      case _ => false
    }

    !(aLessOrEqualB && bLessOrEqualA) || equal
  }

  property("total order is transitive") = forAll { (a: T, b: T, c: T) =>
    val aLessOrEqualB = instance.partialCompare(a, b) match {
      case Some(res) => res != OrdResult.Greater
      case _ => true
    }

    val bLessOrEqualС = instance.partialCompare(b, c) match {
      case Some(res) => res != OrdResult.Greater
      case _ => true
    }

    val notGreater = instance.partialCompare(a, c) match {
      case Some(res) => res != OrdResult.Greater
      case _ => false
    }

    !(aLessOrEqualB && bLessOrEqualС) || notGreater
  }
}
