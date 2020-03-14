package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.util.Random

object Cafe {
  sealed trait Command
  case object Start extends Command

  def apply(cafeConf: Config): Behavior[Command] = {
    val cafeRand: Random = new Random(cafeConf.seed)

    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val waiter = ctx.spawn(Waiter(), "Waiter")
          val customers = (1 to cafeConf.numOfCustomers).map { i => ctx.spawn(Customer(waiter, cafeRand.nextInt(), cafeConf.customerConf), s"Customer$i")}
          val chefs = (1 to cafeConf.numOfChefs).map { i => ctx.spawn(Chef(waiter), s"Chef$i")}

          waiter ! Waiter.SetChefs(chefs.toList)

          customers.foreach { c =>
            c ! Customer.Start
          }
          Behaviors.same
        case _ => Behaviors.same
      }
    }
  }
}
