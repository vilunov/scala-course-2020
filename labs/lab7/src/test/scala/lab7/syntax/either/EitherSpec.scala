package lab7.syntax.either

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EitherSpec extends AnyFlatSpec with Matchers {
  //  import MonoidSyntaxSpec._

  "_.left and _.right" should "Left(_) and Right(_) correspondingly" in {
    1.right shouldEqual Right(1)
    1.right shouldEqual Right(1)

    "abc".left shouldEqual Left("abc")
  }
}
