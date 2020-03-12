package khinkali

import java.util.Calendar

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration
import scala.concurrent.duration.FiniteDuration
import scala.util.Random

case class CafeConf(
    numberOfChefs: Int,
    numberOfCustomers: Int,
    randomConf: RandomConf
)

case class RandomConf(
    seed: Int,
    eatingRange: Range,
    decisionRange: Range,
    khinkaliRange: Range,
    beefTimeRange: Range,
    muttonTimeRange: Range,
    cheeseTimeRange: Range
)

case class Range(start: Int, end: Int) {
  def toRandomDuration(random: Random): FiniteDuration =
    FiniteDuration(random.between(start, end), duration.MILLISECONDS)
  def toRandomInt(random: Random): Int = random.between(start, end)
}

object Cafe {
  sealed trait Command
  case object Start        extends Command
  case object CustomerLeft extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        ConfigSource.default.load[CafeConf] match {
          case Right(cafeConf) =>
            val randomConf = cafeConf.randomConf
            val random     = new Random(randomConf.seed)
            val chefs      = (1 to cafeConf.numberOfChefs).map(i => ctx.spawn(Chef(), name = s"chef$i"))
            val waiter     = ctx.spawn(Waiter(List.from(chefs)), "Waiter")

            val customers = (1 to cafeConf.numberOfCustomers).map { i =>
              val eatingTime   = randomConf.eatingRange.toRandomDuration(random)
              val decisionTime = randomConf.decisionRange.toRandomDuration(random)

              val stuffingDuration = Stuffing
                .getDuration(random, randomConf.beefTimeRange, randomConf.muttonTimeRange, randomConf.cheeseTimeRange)
              val order = Customer.decide(randomConf.khinkaliRange, random, stuffingDuration)

              ctx.spawn(
                Customer(waiter, order, decisionTime, eatingTime, ctx.self),
                s"Customer$i"
              )
            }

            customers.foreach { c => c ! Customer.Start }
            countTime(customers.length, FiniteDuration(Calendar.getInstance().getTimeInMillis, duration.MILLISECONDS))
          case Left(value) =>
            println(s"Error parsing application.config: $value")
            Behaviors.stopped
        }
      case _ => Behaviors.same
    }
  }

  def countTime(customerNumber: Int, startTime: FiniteDuration): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case CustomerLeft =>
        if (customerNumber < 2) {
          println(
            s"All customers served in ${Calendar.getInstance().getTimeInMillis - startTime.toMillis} milliseconds"
          )
          Behaviors.stopped
        } else countTime(customerNumber - 1, startTime)
      case _ => Behaviors.same
    }
  }
}
