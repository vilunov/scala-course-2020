package dogs.syntax.either

import dogs.Main.Gold
import org.scalatest.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class EitherSyntaxSpec extends AnyFlatSpec with Matchers {

  "either" should "be left" in {
    val g = Gold()
    val gold: Either[Gold, Nothing] = g.left
    gold.isLeft shouldBe true
  }

  "either" should "be right" in {
    val harvester = "is under attack"
    val eitherererererererer: Either[Nothing, String] = harvester.right
    eitherererererererer.isRight shouldBe true
  }
}
