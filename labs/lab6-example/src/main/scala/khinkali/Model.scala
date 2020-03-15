package khinkali

import scala.util.Random

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}

case class CookedOrder(orderId: Int, dishes: List[Khinkali])

case class Order(orderId: Int, dishes: List[Khinkali]) {
  def toCookedOrder: CookedOrder =
    CookedOrder(orderId, dishes)
}

case class Khinkali(stuffing: Stuffing, amount: Int)

sealed trait Stuffing

object Stuffing {

  case object Beef extends Stuffing

  case object Mutton extends Stuffing

  case object CheeseAndMushrooms extends Stuffing

}

case class Menu(menu: List[Int => Khinkali]) {
  def apply(i: Int): Int => Khinkali = {
    menu(i)
  }

  def length(): Int = menu.length
}

sealed trait Result

object Result {

  case object Ok extends Result

  case object Busy extends Result

}

object MyRandom {
  // implicits sucked here :c
  private val random = new Random(42) // TODO: change to parameter

  def between(left: Int, right: Int): Int =
    if (left == right)
      right
    else
      random.between(left, right)

  def between(range: IntRange): Int = {
    if (range.left == range.right)
      range.right
    else
      random.between(range.left, range.right)
  }

  def between(range: DoubleRange): Double = {
    if (range.left == range.right)
      range.right
    else
      random.between(range.left, range.right)
  }

}
