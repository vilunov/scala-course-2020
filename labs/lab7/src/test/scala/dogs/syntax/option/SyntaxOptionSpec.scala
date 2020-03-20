package dogs.syntax.option

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SyntaxOptionSpec extends AnyFlatSpec with Matchers {
  "1.some.getOrElse(2)" should "return 1" in {
    1.some.getOrElse(2) shouldEqual 1
  }
}
