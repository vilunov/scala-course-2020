package khinkali

import scala.concurrent.duration
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}
case class Order(orderId: Int, dishes: List[Khinkali]) {
  def getCookingTime(): FiniteDuration =
    dishes.foldLeft(FiniteDuration(0, duration.SECONDS))((time, khinkali) => time + khinkali.cookingTime)
}

case class Khinkali(stuffing: Stuffing, amount: Int, stuffingDuration: Map[Stuffing, FiniteDuration]) {
  def cookingTime: FiniteDuration = stuffingDuration(stuffing) * amount
}

sealed trait Stuffing

object Stuffing {

  case object Beef extends Stuffing

  case object Mutton extends Stuffing

  case object CheeseAndMushrooms extends Stuffing

  def getRandom(random: Random): Stuffing =
    random.between(1, 4) match {
      case 1 => Beef
      case 2 => Mutton
      case 3 => CheeseAndMushrooms
    }

  def getDuration(
      random: Random,
      beefRange: Range,
      muttonRange: Range,
      cheeseRange: Range
  ): Map[Stuffing, FiniteDuration] =
    Map(
      (Beef               -> beefRange.toRandomDuration(random)),
      (Mutton             -> muttonRange.toRandomDuration(random)),
      (CheeseAndMushrooms -> cheeseRange.toRandomDuration(random))
    )
}
