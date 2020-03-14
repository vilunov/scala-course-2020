package khinkali

import khinkali.Stuffing.{Beef, Vegan}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ModelSpec extends AnyFlatSpec with Matchers with OptionValues {

  import ModelSpec._

  "CustomerOrder" should "be able to be converted to Order" in {
    // Arrange
    val customerOrder = CustomerOrder(dishes)

    // Act
    val order = customerOrder.toOrder(20)

    // Assert
    (order.orderId, order.dishes) shouldEqual (20, dishes)
  }

  "Stuffing.toString" should "work correctly" in {
    // Assert
    Stuffing.Beef.toString == "Beef" shouldBe true
    Stuffing.Chicken.toString == "Chicken" shouldBe true
    Stuffing.Vegan.toString == "Vegan" shouldBe true
  }
}

object ModelSpec {
  private val dishes: List[Khinkali] = List(Khinkali(Beef, 5), Khinkali(Vegan, 2))
}