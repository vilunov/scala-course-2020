package dogs

import dogs.CommutativeMonoid._
import dogs.syntax.monoid._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonoidSpec extends AnyFlatSpec with Matchers {
  "Map's commutative monoid combine" should "combine values with common keys" in {
    CommutativeMonoid[Map[Int, Int]]
      .combine(Map(1 -> 2), Map(1 -> 3)) shouldEqual Map(1 -> (2 |+| 3))
  }

  "Int's commutative monoid combine" should "sum values" in {
    CommutativeMonoid[Int].combine(100, 500) shouldEqual 100 + 500
  }

  "Int monoid's unit" should "act as a unit" in {
    (1 |+| CommutativeMonoid[Int].unit) shouldEqual (CommutativeMonoid[Int].unit |+| 1)
    (1 |+| CommutativeMonoid[Int].unit) shouldEqual 1
  }

  "Map's monoid's unit" should "act as a unit" in {
    (Map(0 -> 1) |+| CommutativeMonoid[Map[Int, Int]].unit) shouldEqual (CommutativeMonoid[
      Map[Int, Int]
    ].unit |+| Map(0 -> 1))
    (Map(0 -> 1) |+| CommutativeMonoid[Map[Int, Int]].unit) shouldEqual Map(
      0 -> 1
    )
  }
}
