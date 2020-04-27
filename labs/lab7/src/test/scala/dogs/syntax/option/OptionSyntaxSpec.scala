package dogs.syntax.option

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OptionSyntaxSpec extends AnyFlatSpec with Matchers {

  it should "work for ints" in {
    (10).some  shouldEqual Some(10)
  }

  it should "work for floats" in {
    val a: Float = 1.2f
      a.some  shouldEqual Some(1.2f)
  }

  it should "work for double" in {
    val a :Double = 2.5
    a.some  shouldEqual Some(2.5)
  }

  it should "work for long" in {
    val a :Long= 540
    a.some  shouldEqual Some(540)
  }

  it should "work for strings" in {
    val a = "mew"
    a.some  shouldEqual Some("mew")
  }

  it should "work for list" in {
    val a = List(0,1,2)
    a.some  shouldEqual Some(List(0,1,2))
  }

  it should "work for map" in {
    val a = Map(1->"cat", 2->"dog")
    a.some shouldEqual Some(Map(1->"cat", 2->"dog"))
  }
//
  it should "work for None" in {
    None.some shouldEqual Some(None)
  }

}

