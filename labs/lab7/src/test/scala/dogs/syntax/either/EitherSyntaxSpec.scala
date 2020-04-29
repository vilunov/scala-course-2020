package dogs.syntax.either

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EitherSyntaxSpec extends AnyFlatSpec with Matchers {

  "left" should "work for arbitrary types" in {
    List(1, 2, 3).left shouldEqual Left(List(1, 2, 3))
  }

  "right" should "work for arbitrary types" in {
    Map(1 -> "hello", 2 -> "world").right shouldEqual Right(Map(1 -> "hello", 2 -> "world"))
  }

}

