package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pureconfig.ConfigSource

object Cafe {
  sealed trait Command
  case object Start extends Command
  case object ClientExits extends Command

  var curNumofClients=0

  def apply( numOfChefs: Int, numOfCustomers : Int, conf: CafeConfig): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val r = scala.util.Random
        val waiter = ctx.spawn(Waiter(), "Waiter")
        ctx.log.info(s"waiter created")
        val chefs = (1 to numOfChefs).map { i => ctx.spawn(Chef(waiter, conf, r), s"Chef$i")}
        ctx.log.info(s"chefs created")
        val customers = (1 to numOfCustomers).map { i => ctx.spawn(Customer(waiter, ctx.self, conf, r), s"Customer$i")}
        ctx.log.info(s"customers created")
        curNumofClients = customers.length
        waiter ! Waiter.CameChefs(chefs)

        customers.foreach { c =>
          c ! Customer.Start
        }
        Behaviors.same
      case ClientExits => {
        if (curNumofClients-1==0){
          ctx.log.info(s"last customer exits ")
          ctx.log.info(s"Execution has taken ${System.currentTimeMillis() - ctx.system.startTime} milliseconds")
          ctx.system.terminate()
          Behaviors.same
        }
        else{
          ctx.log.info(s"customer exits ")
          curNumofClients = curNumofClients- 1
          Behaviors.same}
      }
      case _ => Behaviors.same
    }

  }
}
