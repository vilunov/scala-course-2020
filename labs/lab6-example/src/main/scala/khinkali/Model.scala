package khinkali

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}
case class Order(orderId: Int, dishes: List[Khinkali])

case class Khinkali(stuffing: Stuffing, amount: Int)

sealed trait Stuffing

object Stuffing {
  case object Beef extends Stuffing
  case object Mutton extends Stuffing
  case object CheeseAndMushrooms extends Stuffing
}

sealed trait Result
object Result {
  case object Ok extends Result
  case object Busy extends Result
}
