package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object Chef {
  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Waiter.ChefResponse]) extends Command
  case class FinishOrder(order: Order)                                       extends Command

  sealed trait OrderStatus
  object OrderStatus {
    case class Accepted(order: Order) extends OrderStatus
    case class Rejected(order: Order) extends OrderStatus
    case class Finished(order: Order) extends OrderStatus
  }

  def apply(): Behavior[Command] = waitForOrder()

  def waitForOrder(): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo) =>
          replyTo ! Waiter.ChefResponse(OrderStatus.Accepted(order))
          ctx.scheduleOnce(order.getCookingTime, ctx.self, FinishOrder(order))
          produceKhinkali(replyTo)
        case _ => Behaviors.same
      }
    }

  def produceKhinkali(waiter: ActorRef[Waiter.ChefResponse]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo) =>
          replyTo ! Waiter.ChefResponse(OrderStatus.Rejected(order))
          Behaviors.same
        case FinishOrder(order) =>
          waiter ! Waiter.ChefResponse(OrderStatus.Finished(order))
          waitForOrder
      }
    }
}
