package dogs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

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
