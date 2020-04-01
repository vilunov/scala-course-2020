package dogs.syntax.either

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SyntaxEitherSpec extends AnyFlatSpec with Matchers {
  "1.left.isLeft" should "be true" in {
    if (1.left.isLeft) succeed else fail()
  }

  "\"abc\",right" should "match with Right(str == \"abc\")" in {
    "abc".right match {
      case Right(str) => str shouldEqual "abc"
      case _          => fail()
    }
  }
}
