package khinkali

import akka.actor.typed.scaladsl.{Behaviors, StashBuffer}
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Waiter {

  sealed trait Command

  case class TakeOrder(customer: ActorRef[Customer.Eat.type], order: CustomerOrder) extends Command
  case class TryChef(chefId: Int, customer: ActorRef[Customer.Eat.type], order: Order) extends Command
  case object MoveOn extends Command

  implicit val timeout: Timeout = Timeout(1.second)

  var buffer: StashBuffer[Waiter.Command] = _

  def apply(chefs: IndexedSeq[ActorRef[Chef.Command]]): Behavior[Command] = {
    waitForOrder(chefs)
  }

  def waitForOrder(chefs: IndexedSeq[ActorRef[Chef.Command]], lastOrderId: Int = 1): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(customer, order) => {
          // take order, assign id to it
          var acceptedOrder = order.toOrder(lastOrderId)

          // find a chef and dispatch order to him
          ctx.self ! TryChef(0, customer, acceptedOrder)
          waitForOrder(chefs, lastOrderId + 1)
        }

        case TryChef(chefId, customer, order) =>
          val chef = chefs(chefId)
          ctx.ask[Chef.TakeOrder, Result](chef, ref => Chef.TakeOrder(order, ref, customer)) {
            case Success(Result.Ok) =>
              ctx.log.info(s"Dispatched order ${order.orderId} to chef ${chefId + 1}")
              MoveOn

            case Success(Result.Busy) => // try next chef
              // ctx.log.info(s"Chef $chefId is busy, trying next one")
              if ((chefId + 1) == chefs.length) TryChef(0, customer, order)
              else TryChef(chefId + 1, customer, order)

            case Failure(exception) => ??? // waiter machine broke
          }
          Behaviors.same

        case MoveOn => Behaviors.same
        case _ => Behaviors.same
      }
    }
}


