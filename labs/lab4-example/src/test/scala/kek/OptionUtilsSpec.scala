package kek

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OptionUtilsSpec extends AnyFlatSpec with Matchers {
  import OptionUtils._

  "sequence" should "return Some when there are no Nones in the input" in {
    sequence(List(Some(1), Some(3), Some(3), Some(7))) shouldBe Some(List(1, 3, 3, 7))
  }

  it should "return Some if the input is empty" in {
    sequence(List.empty) shouldBe Some(List.empty)
  }

  it should "return None if at least one element is empty" in {
    sequence(List(Some(2), None, Some(8))) shouldBe None
  }
}
