package dogs.syntax.monoid

import dogs.Monoid
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonoidSyntaxSpec  extends AnyFlatSpec with Matchers{
  "int" should "work" in {
    1 |+| 1 shouldEqual 2
    Seq[Int]().reduceMonoid shouldEqual Monoid[Int].unit
    1 |+| Monoid[Int].unit shouldEqual 1

  }

  "float" should "work" in {
    1.0f |+| 1.0f shouldEqual 2.0f
    Seq[Float]().reduceMonoid shouldEqual Monoid[Float].unit
    4.5f |+| Monoid[Float].unit shouldEqual 4.5f

  }

  "double" should "work" in {
    2.0d |+| 2.0d shouldEqual 4.0d
    Seq[Double]().reduceMonoid shouldEqual Monoid[Double].unit
    7.8d |+| Monoid[Double].unit shouldEqual 7.8d

  }

  "long" should "work" in {
    2L |+| 3L shouldEqual 5L
    Seq[Long]().reduceMonoid shouldEqual Monoid[Long].unit
    1L |+| Monoid[Long].unit shouldEqual 1L

  }

  "list" should "work" in {
    List(1) |+| List(2) shouldEqual List(1, 2)
    List("mew") |+| Monoid[List[String]].unit shouldEqual List("mew")
  }

  "map" should "work" in {
    Map(0 -> 1, 1 -> 2, 2 -> 3) |+| Map(1 -> 0, 2 -> 1, 3 -> 2) shouldEqual Map(0 -> 1, 1 -> 2, 2 -> 4, 3 -> 2)
    Map(1 -> 2) |+| Monoid[Map[Int, Int]].unit shouldEqual Map(1 -> 2)
  }

  "reduce and fold" should "work properly" in {
    Seq(1, 2, 3).reduceMonoid shouldEqual 6
    Seq(1, 2, 3).foldSemigroup(4) shouldEqual 10
    Seq(1, 2, 3, 4, 5).foldLeftSemigroup(-15) shouldEqual 0
    Seq(List("mew"), List("gav"), List()).foldRightSemigroup(List("")) shouldEqual List("mew", "gav", "")
  }



}
