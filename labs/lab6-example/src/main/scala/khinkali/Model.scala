package khinkali

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}
case class Order(orderId: Int, dishes: List[Khinkali])

case class Khinkali(stuffing: Stuffing, amount: Int)

sealed trait Stuffing
object Stuffing {
  case object Beef extends Stuffing { override val toString = "Beef" }
  case object Mutton extends Stuffing { override val toString = "Mutton" }
  case object CheeseAndMushrooms extends Stuffing { override val toString = "CheeseAndMushrooms" }
}

sealed trait Result
object Result {
  case object Ok extends Result
  case object Busy extends Result
}
