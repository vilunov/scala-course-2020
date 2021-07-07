package lab7.syntax.monoid

import lab7.Monoid
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonoidSyntaxSpec extends AnyFlatSpec with Matchers {

  "SyntaxSemigroupMonoid (|+| - operator)" should "work as combine for semigroup" in {
    1 |+| 1 shouldEqual 2
    1L |+| 1L shouldEqual 2L
    1.0f |+| 1.0f shouldEqual 2.0f
    1.0d |+| 1.0d shouldEqual 2.0d
    List(1) |+| List(2) shouldEqual List(1, 2)
    Map(0 -> 0, 1 -> 1, 2 -> 2) |+| Map(1 -> (-1), 2 -> (-2), 3 -> 0) shouldEqual Map(1 -> 0, 2 -> 0, 0 -> 0, 3 -> 0)
  }

  "SyntaxIterable" should "work properly" in {
    Seq(1, 2, 3).reduceMonoid shouldEqual 6
    Seq(1, 2, 3).foldSemigroup(0) shouldEqual 6
    Seq(1, 2, 3).foldLeftSemigroup(1) shouldEqual 7
    Seq(List("1"), List("2"), List()).foldRightSemigroup(List("zero")) shouldEqual List("1", "2", "zero")
  }

  "Monoid's |+| with unit" should "work properly" in {
    1 |+| Monoid[Int].neutral shouldEqual 1
    1L |+| Monoid[Long].neutral shouldEqual 1L
    1.0f |+| Monoid[Float].neutral shouldEqual 1.0f
    1.0d |+| Monoid[Double].neutral shouldEqual 1.0d
    List(1) |+| Monoid[List[Int]].neutral shouldEqual List(1)
    Map(0 -> 0) |+| Monoid[Map[Int, Int]].neutral shouldEqual Map(0 -> 0)
  }

  "Monoid's reduce with unit" should "work properly" in {
    Seq[Int]().reduceMonoid shouldEqual Monoid[Int].neutral
  }
}
