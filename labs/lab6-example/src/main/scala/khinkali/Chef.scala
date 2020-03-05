package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import khinkali.Result.{Busy, Ok}

import scala.concurrent.duration._


object Chef {

  sealed trait Command

  case class TakeOrder(order: Order, orderReceiver: ActorRef[Waiter.Command], replyTo: ActorRef[Result]) extends Command

  case class FinishOrder(order: Order) extends Command

  def apply(): Behavior[Command] =
    freeState()

  def freeState(): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case TakeOrder(order, orderReceiver, replyTo) =>
          // notify chefself when he will finish cooking
          ctx.scheduleOnce(1.second, ctx.self, FinishOrder(order)) // TODO
          replyTo ! Ok
          busyState(orderReceiver)
        case _ => Behaviors.same
      }
  }

  def busyState(waiter: ActorRef[Waiter.Command]): Behavior[Command] = Behaviors.receive {
    (_, msg) => {
      msg match {
        case FinishOrder(order) =>
          waiter ! Waiter.DeliverOrder(CookedOrder(order.orderId, order.dishes))
          freeState()
        case TakeOrder(_, _, replyTo) =>
          replyTo ! Busy
          Behaviors.same
      }
    }
  }
}
