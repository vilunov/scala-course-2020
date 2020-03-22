package dogs

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

abstract class PartialOrdProperties[T: Arbitrary](name: String) extends PartialEqualityProperties(name){
//  partiall ordering allows all three properties to be violated, hence there are no properties here
  def instance: PartialOrd[T]
}

abstract class OrdProperties[T: Arbitrary](name: String) extends PartialOrdProperties(name){
  def instance: Ord[T]

  property("total non-strict ordering is reflexive") = forAll {a: T =>
    instance.equal(a, a)
  }

//  todo check if or is really working
  property("total non-strict ordering is antisymmetric") = forAll {(left: T, right: T) =>
    (instance.compare(left,right) != instance.compare(right, left)) || (instance.compare(right,left)==OrdResult.Equal)
  }


  property("total non-strict ordering is transitive") = forAll {(a: T, b: T, c: T) =>
    ((!(instance.compare(a,b)==OrdResult.Less && instance.compare(b,c)==OrdResult.Less) ||
      (instance.compare(a,c) ==OrdResult.Less)) &&

      (!(instance.compare(a,b)==OrdResult.Greater && instance.compare(b,c)==OrdResult.Greater) ||
        (instance.compare(a,c) ==OrdResult.Greater)) &&

      (!(instance.compare(a,b)==OrdResult.Equal && instance.compare(b,c)==OrdResult.Equal ) ||
        (instance.compare(a,c)==OrdResult.Equal)))
  }

}

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

abstract class SemigroupProperties[T: Arbitrary](name:String) extends Properties(name){
  def instance : Semigroup[T]

  property("Assosiative")= forAll{(x:T, y:T, z:T)=>
    instance.combine(x, instance.combine(y, z)) == instance.combine( instance.combine(x, y), z)
  }

}
abstract class CommutativeSemigroupProperties[T: Arbitrary](name:String) extends SemigroupProperties(name){
  def instance : CommutativeSemigroup[T]

  property("Commutative")= forAll{(a:T, b:T)=> instance.combine(a,b)==instance.combine(b,a)}

}
abstract class MonoidProperties[T: Arbitrary](name:String) extends Properties(name){
  def instance : Monoid[T]
  property("Identity")=forAll{(a:T)=> instance.combine(a, instance.unit)==a }
}
abstract class CommutativeMonoidProperties[T: Arbitrary](name:String) extends MonoidProperties(name){
  def instance : CommutativeMonoid[T]
}

