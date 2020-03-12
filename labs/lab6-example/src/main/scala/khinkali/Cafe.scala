package khinkali

import java.time.LocalDateTime

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pureconfig._
import pureconfig.generic.auto._

import scala.util.Random

object Cafe {
  sealed trait Command
  case object Start        extends Command
  case object CustomerLeft extends Command

  def apply(config: Config): Behavior[Command] = {
    val random            = new Random(config.seed)
    var numberOfCustomers = config.numberOfCustomers
    var startTime         = System.currentTimeMillis()

    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val waiter = ctx.spawn(Waiter(), Constants.waiter)
          val chefs = (1 to config.numberOfChefs)
            .map(i => ctx.spawn(Chef(waiter, config.chefConfig, random.nextLong()), s"${Constants.chef}$i"))
          val customers = (1 to config.numberOfCustomers).map { i =>
            ctx.spawn(Customer(waiter, config.customerConfig, random.nextLong()), s"${Constants.customer}$i")
          }

          waiter ! Waiter.Start
          waiter ! Waiter.SetChefs(chefs.toList)

          startTime = System.currentTimeMillis()

          customers.foreach { c =>
            ctx.watchWith(c, CustomerLeft)
            c ! Customer.Start
          }
          Behaviors.same

        case CustomerLeft =>
          numberOfCustomers -= 1
          if (numberOfCustomers == 0) {
            val endTime = System.currentTimeMillis()
            ctx.system.log.info(s"All customers left, the overall processing time is ${endTime - startTime} ms")
          }
          Behaviors.same
      }
    }
  }
}
