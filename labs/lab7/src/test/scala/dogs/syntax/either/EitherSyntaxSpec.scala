package dogs.syntax.either

import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class EitherSyntaxSpec extends AnyFlatSpec with Matchers {

  it should "work for ints" in {
    (10).right shouldEqual Right(10)
    10.left shouldEqual Left(10)
  }

  it should "work for floats" in {
    val a: Float = 1.2f
    a.right shouldEqual Right(1.2f)
    a.left shouldEqual Left(1.2f)
  }

  it should "work for double" in {
    val a :Double = 2.5
    a.right shouldEqual Right(2.5)
    a.left shouldEqual Left(2.5)
  }

  it should "work for long" in {
    val a :Long= 540
    a.right shouldEqual Right(540:Long)
    a.left shouldEqual Left(540:Long)
  }

  it should "work for strings" in {
    val a = "mew"
    a.right shouldEqual Right("mew")
    a.left shouldEqual Left("mew")
  }

  it should "work for list" in {
    val a = List(0,1,2)
    a.right shouldEqual Right(List(0,1,2))
    a.left shouldEqual Left(List(0,1,2))
  }

  it should "work for map" in {
    val a = Map(1->"cat", 2->"dog")
    a.right shouldEqual Right(Map(1->"cat", 2->"dog"))
    a.left shouldEqual Left(Map(1->"cat", 2->"dog"))
  }

//todo NONE??

}

