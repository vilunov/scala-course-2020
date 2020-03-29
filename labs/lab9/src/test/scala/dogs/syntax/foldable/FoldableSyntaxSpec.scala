package dogs.syntax.foldable

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FoldableSyntaxSpec extends AnyFlatSpec with Matchers {
  "sequence" should "work for list of `Some`s" in {
    val initial: List[Option[Int]] = List(Some(1), Some(3), Some(3), Some(7))
    initial.sequence shouldBe Some(List(1, 3, 3, 7))
  }

  it should "work for None in the list" in {
    val initial: List[Option[Int]] = List(Some(1), Some(3), None, Some(7))
    initial.sequence shouldBe None
  }

  it should "work for list of Vectors" in {
    val initial: List[Vector[Int]] = List(Vector(1, 3, 3, 7), Vector(2, 2, 8))
    initial.sequence shouldBe Vector(
      List(1, 2), List(1, 2), List(1, 8),
      List(3, 2), List(3, 2), List(3, 8),
      List(3, 2), List(3, 2), List(3, 8),
      List(7, 2), List(7, 2), List(7, 8),
    )
  }
}
