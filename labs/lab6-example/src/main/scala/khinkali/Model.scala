package khinkali

import akka.actor.typed.ActorRef

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
  case class Ok(ordered:CustomerOrder, cus :ActorRef[Customer.Eat.type], chef: ActorRef[Chef.Command]) extends Result
  case class Busy(ordered:CustomerOrder, cus :ActorRef[Customer.Eat.type ]) extends Result
}
