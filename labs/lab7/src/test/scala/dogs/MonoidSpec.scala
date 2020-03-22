package dogs

import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpec

// todo: я хз как избавиться от очевидного оверфлоу
//object MonoidIntSpec extends MonoidProperties[Int]("IntMonoid") {
//  override def instance: Monoid[Int] = Monoid[Int]
//}
//
//object MonoidLongSpec extends MonoidProperties[Long]("LongMonoid") {
//  override def instance: Monoid[Long] = Monoid[Long]
//}
//
//object MonoidFloatSpec extends MonoidProperties[Float]("FloatMonoid") {
//  override def instance: Monoid[Float] = Monoid[Float]
//}
//
//object MonoidDoubleSpec extends MonoidProperties[Double]("DoubleMonoid") {
//  override def instance: Monoid[Double] = Monoid[Double]
//}


// todo: I hereby declare, I've stolen those tests
class MonoidSpec extends AnyFlatSpec with Matchers {

  "combine" should "work correctly for implemented semigroup types" in {
    Semigroup[Int].combine(5, 5) shouldEqual 10
    Semigroup[Long].combine(5, 5) shouldEqual 10
    Semigroup[Float].combine(5.5f, 5.5f) shouldEqual 11f
    Semigroup[Double].combine(5, 5) shouldEqual 10
    Semigroup[List[Int]].combine(List(1), List(2)) shouldEqual List(1, 2)
    Semigroup[Map[Int, List[Int]]]
      .combine(Map(1 -> List(1)), Map(1 -> List(2), 2 -> List(3))) shouldEqual Map(1 -> List(1, 2), 2 -> List(3))
  }

  "combine" should "work correctly for implemented monoid types" in {
    Monoid[Int].combine(5, Monoid[Int].unit) shouldEqual 5
    Monoid[Long].combine(5, 5) shouldEqual 10
    Monoid[Float].combine(5.5f, 5.5f) shouldEqual 11f
    Monoid[Double].combine(5, 5) shouldEqual 10
    Monoid[List[Int]].combine(List(1), List(2)) shouldEqual List(1, 2)
    Monoid[Map[Int, List[Int]]]
      .combine(Map(1 -> List(1)), Monoid[Map[Int, List[Int]]].unit) shouldEqual Map(1 -> List(1))
  }

  "combine" should "work correctly for implemented commutative semigroup types" in {
    CommutativeSemigroup[Int].combine(5, Monoid[Int].unit) shouldEqual 5
    CommutativeSemigroup[Long].combine(5, 5) shouldEqual 10
    CommutativeSemigroup[Float].combine(5.5f, 5.5f) shouldEqual 11f
    CommutativeSemigroup[Double].combine(5, 5) shouldEqual 10
    CommutativeSemigroup[Map[Int, Int]]
      .combine(Map(1 -> 1), Map(2 -> 5, 1 -> 1)) shouldEqual Map(1 -> 2, 2 -> 5)
  }

  "combine" should "work correctly for implemented commutative monoid types" in {
    CommutativeMonoid[Int].combine(5, Monoid[Int].unit) shouldEqual 5
    CommutativeMonoid[Long].combine(5, 5) shouldEqual 10
    CommutativeMonoid[Float].combine(5.5f, 5.5f) shouldEqual 11f
    CommutativeMonoid[Double].combine(5, 5) shouldEqual 10
    CommutativeMonoid[Map[Int, Int]]
      .combine(Map(1 -> 1), Map(2 -> 5, 1 -> 1)) shouldEqual Map(1 -> 2, 2 -> 5)
    CommutativeMonoid[Map[Int, Int]]
      .combine(Map(2 -> 5, 1 -> 1), Map(1 -> 1)) shouldEqual Map(1 -> 2, 2 -> 5)
  }
}