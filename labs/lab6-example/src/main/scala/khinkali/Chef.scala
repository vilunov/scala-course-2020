package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object Chef {
//  Waiting for an order that is passed from Waiter
//  Cooking the order:
//    While cooking, no orders can be taken
//    Cooking takes random time
//    Each dish has its own randomness
//    Number of dishes proporionally affects the cooking time
  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  def apply(waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    waitForOrder(waiter)

  def waitForOrder(waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          ctx.log.info(s"Chef takes the order {} for customer {}", order.orderId, customer)
          // посмотреть заказ, посчитать сколько времени уйдет и сказать когда закончим
          ctx.scheduleOnce(1.second, ctx.self, FinishOrder(order.orderId, customer))
          replyTo ! Result.Ok
          finishOrder(waiter)
        case _ => Behaviors.same
      }
    }

  def finishOrder(waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case FinishOrder(orderId, customer) =>
          ctx.log.info(s"Order {} is ready for customer {}", orderId, customer)
          waiter ! Waiter.ServeOrder(customer)
          waitForOrder(waiter)
        case TakeOrder(_, replyTo, _) =>
          replyTo ! Result.Busy
          Behaviors.same
        case _ => Behaviors.same
      }
  }
}
