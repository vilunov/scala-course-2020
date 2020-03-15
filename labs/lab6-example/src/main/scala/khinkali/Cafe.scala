package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.util.Random

object Cafe {
  sealed trait Command
  case object Start extends Command

  def apply(config: Config): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val random = new Random(config.randomSeed)

        val chefs = (1 to config.nChefs).map { i => ctx.spawn(Chef(random, config.minCookingTime, config.maxCookingTime), s"Chef$i")}

        val waiter = ctx.spawn(Waiter(chefs), "Waiter")

        val customers = (1 to config.nCustomers).map { i => {
          val nDishes = config.minDishes + random.nextInt((config.maxDishes - config.minDishes + 1))
          val order = (1 to nDishes).map {
            _ => {
              val stuffing = random.nextInt().abs % 3 match {
                case 0 => Stuffing.Beef
                case 1 => Stuffing.CheeseAndMushrooms
                case 2 => Stuffing.Mutton
              }
              val amount = config.minKhinkalis + random.nextInt((config.maxKhinkalis - config.minKhinkalis + 1))
              Khinkali(stuffing, amount)
            }
          }.toList
          ctx.spawn(Customer(waiter, CustomerOrder(order), random,
            config.minSelectingTime, config.maxEatingTime,
            config.minEatingTime, config.maxEatingTime), s"Customer$i")
        }}

        customers.foreach { c =>
          c ! Customer.Start
        }
        Behaviors.same
    }
  }
}
