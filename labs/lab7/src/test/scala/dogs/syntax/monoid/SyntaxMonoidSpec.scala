package dogs.syntax.monoid

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SyntaxMonoidSpec extends AnyFlatSpec with Matchers {
  "19 |+| 20" should "return 39" in {
    19 |+| 20 shouldEqual 39
  }

  "List(1, 2, 7).reduceMonoid" should "return 10" in {
    List(1, 2, 7).reduceMonoid shouldEqual 10
  }

  "List(List(1), List(2), List(3)).foldLeftSemigroup(List())" should "return List(1, 2, 3)" in {
    List(List(1), List(2), List(3))
      .foldLeftSemigroup(List()) shouldEqual List(1, 2, 3)
  }
}
