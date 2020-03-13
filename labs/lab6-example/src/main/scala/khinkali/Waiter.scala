package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import khinkali.Chef.TakeOrder

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Waiter {
  sealed trait Command

  case class TakeOrder(order: CustomerOrder, customer: ActorRef[Customer.Command]) extends Command
  case class ServeOrder(customer: ActorRef[Customer.Eat.type]) extends Command
  case class LookForChef(order: Order, customer: ActorRef[Customer.Eat.type], chefsToLookThrough: List[ActorRef[Chef.Command]]) extends Command
  case object Continue extends Command
  case class SetChefs(chefs: List[ActorRef[Chef.Command]]) extends Command

  var chefs: List[ActorRef[Chef.Command]] = List()
  var lastOrderId: Int = 0

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case SetChefs(chefs) =>
          this.chefs = chefs
          Behaviors.same

        case TakeOrder(order, customer) =>
          val currOrder = order.toOrder(lastOrderId)
          lastOrderId += 1
          ctx.self ! LookForChef(currOrder, customer, chefs)
          Behaviors.same

        case LookForChef(order, customer, chef +: chefs) =>
          ctx.ask(chef, (ref: ActorRef[Result]) => Chef.TakeOrder(order, ref, customer)) {
            case Success(Result.Ok) =>
              Continue
            case Success(Result.Busy) =>
              if (chefs.isEmpty) {
                ctx.scheduleOnce(1.second, ctx.self, LookForChef(order, customer, this.chefs))
                Continue
              } else {
                LookForChef(order, customer, chefs)
              }
            case Failure(exception) =>
              Continue
            case _ => Continue
          }
          Behaviors.same

        case ServeOrder(customer) =>
          customer ! Customer.Eat
          Behaviors.same

        case _ => Behaviors.same
      }
    }
}
