package dogs

import dogs.syntax.monadic._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonadSpec extends AnyFlatSpec with Matchers {

  "Function1 monad" should "work" in {
    val first: String => Int = _ => 3

    val second: String => String = "Hello, " + _ + "! "

    def third(i: Int, j: String): String => String = _ => j * i

    val composed = for {
      num <- first
      greeting <- second
      result <- third(num, greeting)
    } yield result

    composed("Kek") shouldBe "Hello, Kek! Hello, Kek! Hello, Kek! "
  }
}
