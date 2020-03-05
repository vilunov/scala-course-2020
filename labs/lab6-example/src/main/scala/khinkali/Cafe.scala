package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Stuffing.{Beef, CheeseAndMushrooms, Mutton}


object Cafe {

  sealed trait Command

  case object Start extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val chefs = (1 to 2).map(i => ctx.spawn(Chef(), name = s"Rustem$i")).toList
        val waiter = ctx.spawn(Waiter(chefs), "Waiter")
        val menu = Menu(List(Khinkali(Beef, _), Khinkali(CheeseAndMushrooms, _), Khinkali(Mutton, _)))
        // TODO: parametrise
        val customers = (1 to 10).map { i => ctx.spawn(Customer(waiter, menu), s"Customer$i") }

        customers.foreach { c =>
          c ! Customer.Start
        }
        Behaviors.same
    }
  }
}
