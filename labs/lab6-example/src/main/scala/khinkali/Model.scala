package khinkali

import scala.util.Random

case class CustomerOrder(dishes: List[Khinkali]) {
  def toOrder(id: Int): Order = Order(id, dishes)
}

case class CookedOrder(orderId: Int, dishes: List[Khinkali])

case class Order(orderId: Int, dishes: List[Khinkali])

case class Khinkali(stuffing: Stuffing, amount: Int)

sealed trait Stuffing

object Stuffing {

  case object Beef extends Stuffing

  case object Mutton extends Stuffing

  case object CheeseAndMushrooms extends Stuffing

}

sealed case class Menu(menu: List[Int => Khinkali]) {
  def apply(menu: List[Int => Khinkali]): Menu = {
    require(menu.nonEmpty)
    Menu(menu)
  }
}

sealed trait Result

object Result {

  case object Ok extends Result

  case object Busy extends Result

}

object Random {
  private val random = new Random(42) // TODO: change to parameter

  def between(left: Int, right: Int): Int =
    random.between(left, right)
}

class RoundRobin[T](items: Seq[T]) {
  require(items.nonEmpty)
  private val infiniteStream = LazyList.continually(items.to(LazyList)).flatten

  def next(): T = infiniteStream.take(1).toList(0)
}
