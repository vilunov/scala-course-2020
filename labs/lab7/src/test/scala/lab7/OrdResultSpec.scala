package lab7

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OrdResultSpec extends AnyFlatSpec with Matchers {
  "inverse" should "be equal for equal" in {
    OrdResult.Equal.inverse shouldBe OrdResult.Equal
  }

  it should "be less for greater" in {
    OrdResult.Greater.inverse shouldBe OrdResult.Less
  }

  it should "be greater for less" in {
    OrdResult.Less.inverse shouldBe OrdResult.Greater
  }
}
