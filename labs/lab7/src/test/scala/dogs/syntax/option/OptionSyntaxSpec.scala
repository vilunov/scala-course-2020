package dogs.syntax.option

import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class OptionSyntaxSpec  extends AnyFlatSpec with Matchers {

  "either" should "be left" in {
    val thesis = "done"
    val optioned: Option[String] = thesis.some
    optioned.isEmpty shouldBe false
    optioned.get shouldEqual thesis
  }
}
