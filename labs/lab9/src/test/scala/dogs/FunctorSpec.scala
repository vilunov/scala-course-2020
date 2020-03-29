package dogs

import dogs.syntax.monadic._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class FunctorSpec extends AnyFlatSpec with Matchers {
  def applyMap[F[+_] : Functor](source: F[Int]): F[String] =
    source.map(_.toString)

  "Map functor" should "work for K = Int" in {
    val initial: Map[Int, Int] = Map(1 -> 2, 2 -> 4)
    applyMap(initial) shouldBe Map(1 -> "2", 2 -> "4")
  }

  it should "work for K = String" in {
    val initial: Map[String, Int] = Map("a" -> 2, "b" -> 4)
    applyMap(initial) shouldBe Map("a" -> "2", "b" -> "4")
  }

  "Either functor" should "work for left" in {
    val initial: Either[Double, Int] = Left(1.0)
    applyMap(initial) shouldBe initial
  }

  it should "work for right" in {
    val initial: Either[Double, Int] = Right(1337)
    applyMap(initial) shouldBe Right("1337")
  }
}
