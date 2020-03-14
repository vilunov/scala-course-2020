package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

import scala.util.Random


object Cafe {

  sealed trait Command

  case object Start extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val config = ConfigSource.default.load[Config].getOrElse(
          Config(1, 5, 2, 0.5, 1.5, 1, 2, 1, 2, 0.5, 1.5, 0.2, 0.5)
        )
        val random = new Random(config.randomSeed)
        val chefs = (1 to config.nChefs).map { i => ctx.spawn(Chef(config, random), s"Chef$i") }
        val waiter = ctx.spawn(Waiter(chefs), "Waiter")
        val customers = (1 to config.nCustomers).map { i =>
          ctx.spawn(
            Customer(config, random, waiter, CustomerOrder(List(Khinkali(Stuffing.Beef, 10)))),
            s"Customer$i")
        }

        customers.foreach { c =>
          c ! Customer.Start
        }
        Behaviors.same
    }
  }
}
