package dogs.syntax.monoid

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonoidSyntaxSpec extends AnyFlatSpec with Matchers {

  "combine" should "work for correctly" in {
    5 |+| 15 shouldEqual 20
    List(1, 2, 3) |+| List(4, 5) shouldEqual List(1, 2, 3, 4, 5)
    Map(1 -> 15) |+| Map(0 -> 5, 1 -> 10) shouldEqual Map(0 -> 5, 1 -> 25)
  }

  "reduceMonoid" should "work correctly" in {
    List(2, 3).reduceMonoid shouldEqual 5
  }

  "reduceMonoid" should "work correctly for empty list" in {
    List.empty[Int].reduceMonoid shouldEqual 0
  }

  "foldSemigroup" should "word correctly" in {
    List(1, 2, 3).foldSemigroup(1) shouldEqual 7
  }

  "foldLeftSemigroup" should "word correctly" in {
    List(List(1), List(2), List(3)).foldLeftSemigroup(List.empty[Int]) shouldEqual List(1, 2, 3)
  }

  "foldRightSemigroup" should "word correctly" in {
    List(List(1), List(2), List(3)).foldRightSemigroup(List.empty[Int]) shouldEqual List(1, 2, 3)
  }

}