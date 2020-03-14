package khinkali

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}
case class Order(orderId: Int, dishes: List[Khinkali])

case class Khinkali(stuffing: Stuffing, amount: Int)

sealed trait Stuffing

object Stuffing {
  case object Beef extends Stuffing { override val toString = "Beef" }
  case object Chicken extends Stuffing { override val toString = "Chicken" }
  case object Vegan extends Stuffing { override val toString = "Vegan" }
}

sealed trait Result
object Result {
  case object Ok extends Result
  case object Busy extends Result
}

// Configuration models
sealed trait CookingTimes
case class BeefTime(from: Double, to: Double) extends CookingTimes
case class ChickenTime(from: Double, to: Double) extends CookingTimes
case class VeganTime(from: Double, to: Double) extends CookingTimes

final case class CafeConfig(guestsCount: Int,
                            chefsCount: Int,
                            waitingTime: (Double, Double),
                            cookingTimes: List[CookingTimes],
                            maxDishes: Int,
                            seed: Int)