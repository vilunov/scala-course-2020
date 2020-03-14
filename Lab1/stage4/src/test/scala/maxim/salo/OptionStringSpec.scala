package maxim.salo

import org.scalatest.{FlatSpec, Matchers}

class OptionStringSpec extends FlatSpec with Matchers {

  "map" should "be correct for SomeString => 'LOVE SCALA'" in {
    val x   = SomeString("some string")
    val res = x.map(_ => "LOVE SCALA")
    res shouldEqual SomeString("LOVE SCALA")
  }

  "map" should "be correct for NoneString => 'LOVE SCALA'" in {
    val x   = NoneString
    val res = x.map(_ => "LOVE SCALA")
    res shouldEqual NoneString
  }

  "flatMat" should "be correct for SomeString => SomeString('ONO RABOTAET')" in {
    val x   = SomeString("some string")
    val res = x.flatMap(_ => SomeString("ONO RABOTAET"))
    res shouldEqual SomeString("ONO RABOTAET")
  }

  "flatMat" should "be correct for NoneString => SomeString('ONO RABOTAET')" in {
    val x   = NoneString
    val res = x.flatMap(_ => SomeString("ONO RABOTAET"))
    res shouldEqual NoneString
  }

  "filter" should "be correct for SomeString => _.contains == true" in {
    val x   = SomeString("some string")
    val res = x.filter(str => str.contains("me s"))
    res shouldEqual x
  }

  "filter" should "be correct for SomeString => _.contains == false" in {
    val x   = SomeString("some string")
    val res = x.filter(str => str.contains("MENYA TUT NET"))
    res shouldEqual NoneString
  }

  "getOrElse" should "be correct for SomeString" in {
    val x   = SomeString("some string")
    val res = x.getOrElse("tut bilo pusto")
    res shouldEqual x.value
  }

  "getOrElse" should "be correct for NoneString" in {
    val expected = "tut bilo pusto"
    val x        = NoneString
    val res      = x.getOrElse(expected)
    res shouldEqual expected
  }
}
