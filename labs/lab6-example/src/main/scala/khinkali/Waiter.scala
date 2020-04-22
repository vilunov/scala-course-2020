package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Waiter {
  sealed trait Command
  case object Start                                                                  extends Command
  case object Continue                                                               extends Command
  case class SetChefs(chefs: List[ActorRef[Chef.Command]])                           extends Command
  case class AcceptOrder(order: CustomerOrder, customer: ActorRef[Customer.Command]) extends Command
  case class RequestChief(order: Order, customer: ActorRef[Customer.Command], chefsToAsk: List[ActorRef[Chef.Command]])
      extends Command
  case class ServeOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  // Based on the fact that there is only one waiter in the system.
  // And also on the fact that I can't no more with all this refactoring ;_;
  implicit val timeout: Timeout           = Timeout(1.second)
  var id                                  = 0
  var chefs: List[ActorRef[Chef.Command]] = List()

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        ctx.log.info("Waiter started serving")
        Behaviors.same

      case SetChefs(newChefs) =>
        chefs = newChefs
        ctx.log.info("Setting chefs")
        Behaviors.same

      case AcceptOrder(order, customer) =>
        val acceptedOrder = order.toOrder(id)
        id += 1
        ctx.log.info(s"Order with id ${acceptedOrder.orderId} accepted, starting looking for a chef")
        ctx.self ! RequestChief(acceptedOrder, customer, chefs)
        Behaviors.same

      case RequestChief(order, customer, chef +: chefs) =>
        ctx.ask(chef, (ref: ActorRef[Result]) => Chef.TakeOrder(order, ref, customer)) {
          case Success(Result.Ok) =>
            ctx.log.info(s"Chef accepted order with id ${order.orderId}")
            Continue
          case Success(Result.Busy) =>
            if (chefs.nonEmpty) {
              RequestChief(order, customer, chefs)
            } else {
              ctx.scheduleOnce(1.second, ctx.self, RequestChief(order, customer, chefs))
              Continue
            }
          case Failure(exception) =>
            println(exception)
            Continue
        }
        Behaviors.same

      case ServeOrder(id, customer) =>
        ctx.log.info(s"Serving order with id $id")
        customer ! Customer.Eat
        Behaviors.same

      case _ => Behaviors.same
    }
  }
}
