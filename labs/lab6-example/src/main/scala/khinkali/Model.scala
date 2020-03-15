package khinkali

import scala.util.Random

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}

object CustomerOrder {
  def generateOrder(rand: Random, customerConf: CustomerConf): CustomerOrder = {
    CustomerOrder(
      List.from(
        (1 to customerConf.numOfDishesRange.inRange(rand))
          .map[Khinkali](_ => Khinkali(
              Stuffing.getStuffing(rand),
              customerConf.numOfKhinkalisRange.inRange(rand)
            )
          )
      )
    )
  }
}

case class Order(orderId: Int, dishes: List[Khinkali])

case class Khinkali(stuffing: Stuffing, amount: Int)

sealed trait Stuffing

object Stuffing {
  case object Beef extends Stuffing
  case object Mutton extends Stuffing
  case object CheeseAndMushrooms extends Stuffing

  def getStuffing(random: Random): Stuffing =
    random.between(1, 4) match {
      case 1 => Beef
      case 2 => Mutton
      case 3 => CheeseAndMushrooms
    }
}

sealed trait Result
object Result {
  case object Ok extends Result
  case object Busy extends Result
}
