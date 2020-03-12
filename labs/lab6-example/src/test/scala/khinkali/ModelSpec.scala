package khinkali

import khinkali.Stuffing.{Beef, CheeseAndMushrooms, Mutton}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class ModelSpec extends AnyFlatSpec with Matchers {
  "order cooking time" should "be calculated correctly" in {
    val durationMap =
      Map[Stuffing, FiniteDuration](Beef -> 1.second, Mutton -> 2.seconds, CheeseAndMushrooms -> 3.seconds)
    val khinkali = List(
      Khinkali(Beef, 2, durationMap),
      Khinkali(Mutton, 4, durationMap),
      Khinkali(Mutton, 3, durationMap),
      Khinkali(CheeseAndMushrooms, 5, durationMap)
    )
    val order = CustomerOrder(khinkali).toOrder(0)
    order.getCookingTime shouldBe 31.seconds
  }
}
