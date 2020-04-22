package khinkali

import akka.actor.typed.ActorRef

import scala.util.Random

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}

object CustomerOrder {
  def generateOrder(random: Random, customerConfig: CustomerConfig): CustomerOrder = {
    CustomerOrder(
      (1 to customerConfig.orderSizeBoundaries.randomWithin(random))
        .map[Khinkali](_ => Khinkali(random.nextInt(), customerConfig.khinkaliAmountBoundaries.randomWithin(random)))
        .toList
    )
  }
}
case class Order(orderId: Int, dishes: List[Khinkali]) {
  def preparationTime(random: Random, cookingTimeBoundaries: Boundaries): Int = {
    this.dishes.map(_.amount * cookingTimeBoundaries.randomWithin(random)).sum
  }
}

case class Khinkali(stuffing: Stuffing, amount: Int)

sealed trait Stuffing

object Stuffing {
  case object Beef               extends Stuffing
  case object Mutton             extends Stuffing
  case object CheeseAndMushrooms extends Stuffing

  implicit def intToStuffing(x: Int): Stuffing = {
    x % 3 match {
      case 0 => Beef
      case 1 => Mutton
      case _ => CheeseAndMushrooms
    }
  }
}

sealed trait Result
object Result {
  case object Ok   extends Result
  case object Busy extends Result
}
