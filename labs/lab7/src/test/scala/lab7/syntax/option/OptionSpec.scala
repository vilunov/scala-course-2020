package lab7.syntax.option

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OptionSpec extends AnyFlatSpec with Matchers {
  //  import MonoidSyntaxSpec._

  "_.some" should "Some(_)" in {
    1.some shouldEqual Some(1)
    "abc".some shouldEqual Some("abc")
    Seq(1, 2, 3).some shouldEqual Some(Seq(1, 2, 3))
    None.some shouldEqual Some(None)
  }
}
