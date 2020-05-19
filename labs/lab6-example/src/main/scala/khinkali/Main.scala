package khinkali

import akka.actor.typed.ActorSystem

import pureconfig._
import pureconfig.generic.auto._

case class Range(start: Int, end: Int)
case class TimeRange(start: Float, end: Float)
case class CustomerConfig(
    numDishes: Range,
    dishAmount: Range,
    orderingTime: TimeRange,
    eatingTime: TimeRange
)
case class ChefConfig(
    beef: TimeRange,
    mutton: TimeRange,
    cheeseAndMushrooms: TimeRange
)
case class CafeConfig(
    seed: Long,
    numChefs: Int,
    numCustomers: Int,
    customer: CustomerConfig,
    chef: ChefConfig
)

object Main extends App {
  ConfigSource.default.load[CafeConfig] match {
    case Right(config) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(config), "Cafe")
      system ! Cafe.Start
    case Left(e) => println(e)
  }
}
