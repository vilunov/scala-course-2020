package khinkali

import akka.actor.typed.ActorSystem
import pureconfig._
import pureconfig.generic.auto._

case class CookingTime(avg: Int, varia: Int)
case class SelectingTime(avg: Int, varia: Int)
case class EatingTime(avg: Int, varia: Int)
case class OrderedDishes(avg: Int, varia: Int)
case class KhinkalisInDish(avg: Int, varia: Int)

case class CustomerConfig(
  selectingTime: SelectingTime,
  eatingTime: EatingTime,
)

case class OrderConfig(
  orderedDishes: OrderedDishes,
  khinkalisInDish: KhinkalisInDish
)

case class cafeConf(
  randomSeed: Int,
  nCustomers: Int,
  nChefs: Int,

  cookingTime: CookingTime,

  customerConfig: CustomerConfig,
  orderConfig: OrderConfig
)

object Main extends App {
  var conf = ConfigSource.default.load[cafeConf]
  conf match {
    case Left(value) =>
      print(s"Unable to load config: $value")

    case Right(value) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(value), "Cafe")
      system ! Cafe.Start
  }
}
