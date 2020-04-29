package dogs.syntax.option

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OptionSyntaxSpec extends AnyFlatSpec with Matchers {

  "some" should "work for arbitrary types" in {
    List(1, 2, 3).some shouldEqual Some(List(1, 2, 3))
    val a = 579
    a.some shouldEqual Some(579)
  }

}
