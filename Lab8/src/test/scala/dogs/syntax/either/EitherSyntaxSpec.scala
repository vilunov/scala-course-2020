package dogs.syntax.either

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EitherSyntaxSpec extends AnyFlatSpec with Matchers {

  "Left" should "work for some type" in {
    val map = Map("a" -> 1, "b" -> 4)
    map.left shouldEqual Left(map)
  }

  "Right" should "work for some type" in {
    val map = Map("a" -> 1, "b" -> 4)
    map.right shouldEqual Right(map)
  }
}
