package lab7

import org.scalacheck.{Arbitrary, Properties}
import org.scalacheck.Prop.forAll
//import lab7._

abstract class SemigroupProperties[T: Arbitrary](name: String)(implicit instance: Semigroup[T])
  extends Properties(name) {
  //  abstract def instance: Semigroup[T]

  property("Associativity") = forAll { (a: T, b: T, c: T) =>
    instance.combine(instance.combine(a, b), c) == instance.combine(a, instance.combine(b, c))
  }
}

abstract class CommutativeSemigroupProperties[T: Arbitrary](name: String)(implicit instance: CommutativeSemigroup[T])
  extends SemigroupProperties[T](name) {

  property("Commutativity") = forAll {
    (a: T, b: T) => instance.combine(a, b) == instance.combine(b, a)
  }
}