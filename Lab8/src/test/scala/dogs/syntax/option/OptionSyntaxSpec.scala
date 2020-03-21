package dogs.syntax.option

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OptionSyntaxSpec extends AnyFlatSpec with Matchers {

  "Some" should "work for some type" in {
    val map = Map("a" -> 1, "b" -> 4)
    map.some shouldEqual Some(map)
  }
}
