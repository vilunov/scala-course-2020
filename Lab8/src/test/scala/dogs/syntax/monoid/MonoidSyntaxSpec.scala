package dogs.syntax.monoid

import dogs.{Monoid, Semigroup}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonoidSyntaxSpec extends AnyFlatSpec with Matchers {

  "|+|" should "work for some type" in {
    val map1 = Map("a" -> 1, "b" -> 4)
    val map2 = Map("a" -> 5, "c" -> 3)
    map1 |+| map2 shouldEqual Semigroup[Map[String, Int]].combine(map1, map2)
  }

  "ReduceMonoid" should "work for some type" in {
    val list = List(4, 3, 5, 1)
    list.reduceMonoid shouldEqual list.reduceLeftOption(Monoid[Int].combine).getOrElse(Monoid[Int].unit)
  }

  "FoldSemigroup" should "work for some type" in {
    val list = List(4, 3, 5, 1)
    list.foldSemigroup(0) shouldEqual list.fold(0)(Semigroup[Int].combine)
  }

  "FoldLeftSemigroup" should "work for some type" in {
    val list = List(4, 3, 5, 1)
    list.foldLeftSemigroup(0) shouldEqual list.foldLeft(0)(Semigroup[Int].combine)
  }

  "FoldRightSemigroup" should "work for some type" in {
    val list = List(4, 3, 5, 1)
    list.foldRightSemigroup(0) shouldEqual list.foldRight(0)(Semigroup[Int].combine)
  }
}
