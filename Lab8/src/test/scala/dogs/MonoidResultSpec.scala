package dogs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonoidResultSpec extends AnyFlatSpec with Matchers {

  "Combine" should "work for some semigroup types" in {
    Semigroup[Int].combine(2, 2) shouldEqual 4
    Semigroup[Long].combine(2L, 2L) shouldEqual 4L
    Semigroup[Float].combine(2F, 2F) shouldEqual 4F
    Semigroup[Double].combine(2D, 2D) shouldEqual 4D
    Semigroup[List[Int]].combine(List(1), List(2)) shouldEqual List(1, 2)
    Semigroup[Map[Int, List[Int]]]
      .combine(Map(1 -> List(1)), Map(1 -> List(2), 2 -> List(3))) shouldEqual Map(1 -> List(1, 2), 2 -> List(3))
  }

  "Combine" should "work for some monoid types" in {
    Monoid[Int].combine(2, Monoid[Int].unit) shouldEqual 2
    Monoid[Long].combine(2L, 2L) shouldEqual 4L
    Monoid[Float].combine(2F, 2F) shouldEqual 4F
    Monoid[Double].combine(2D, 2D) shouldEqual 4D
    Monoid[List[Int]].combine(List(1), List(2)) shouldEqual List(1, 2)
    Monoid[Map[Int, List[Int]]]
      .combine(Map(1 -> List(1)), Monoid[Map[Int, List[Int]]].unit) shouldEqual Map(1 -> List(1))
  }

  "Combine" should "work for some commutative semigroup types" in {
    CommutativeSemigroup[Int].combine(2, 2) shouldEqual 4
    CommutativeSemigroup[Long].combine(2L, 2L) shouldEqual 4L
    CommutativeSemigroup[Float].combine(2F, 2F) shouldEqual 4F
    CommutativeSemigroup[Double].combine(2D, 2D) shouldEqual 4D
    CommutativeSemigroup[Map[Int, Int]]
      .combine(Map(1 -> 1), Map(2 -> 5, 1 -> 1)) shouldEqual Map(1 -> 2, 2 -> 5)
  }

  "Combine" should "work for some commutative monoid types" in {
    CommutativeMonoid[Int].combine(2, Monoid[Int].unit) shouldEqual 2
    CommutativeMonoid[Long].combine(2L, 2L) shouldEqual 4L
    CommutativeMonoid[Float].combine(2F, 2F) shouldEqual 4F
    CommutativeMonoid[Double].combine(2D, 2D) shouldEqual 4D
    CommutativeMonoid[Map[Int, Int]]
      .combine(Map(1 -> 1), Map(2 -> 5, 1 -> 1)) shouldEqual Map(1 -> 2, 2 -> 5)
    CommutativeMonoid[Map[Int, Int]]
      .combine(Map(2 -> 5, 1 -> 1), Map(1 -> 1)) shouldEqual Map(1 -> 2, 2 -> 5)
  }
}
