package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Cafe {
  sealed trait Command
  case object Start extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val waiter = ctx.spawn(Waiter(), "Waiter")
        val customers = (1 to 10).map { i => ctx.spawn(Customer(waiter, CustomerOrder(List(Khinkali(Stuffing.Beef, 10)))), s"Customer$i")}
        val chefs = (1 to 10).map { i => ctx.spawn(Chef(waiter), s"Chef$i")}

        waiter ! Waiter.SetChefs(chefs.toList)

        customers.foreach { c =>
          c ! Customer.Start
        }
        Behaviors.same
      case _ => Behaviors.same
    }
  }
}
