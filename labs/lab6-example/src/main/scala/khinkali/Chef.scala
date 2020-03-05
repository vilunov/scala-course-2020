package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import khinkali.Result.{Busy, Ok}

import scala.concurrent.duration._


object Chef {

  sealed trait Command

  case class TakeOrder(order: Order, orderReceiver: ActorRef[Waiter.Command], replyTo: ActorRef[Result]) extends Command

  case class FinishOrder(order: Order) extends Command

  def apply(conf: ChefConf): Behavior[Command] =
    freeState(conf)

  def freeState(conf: ChefConf): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case TakeOrder(order, orderReceiver, replyTo) =>
          // notify chefself when he will finish cooking
          ctx.scheduleOnce(MyRandom.between(conf.cookingTimeRange).second, ctx.self, FinishOrder(order))
          replyTo ! Ok
          ctx.log.info(s"Accepted order #${order.orderId}. Now I'm busy!")
          busyState(orderReceiver, conf)
        case _ => Behaviors.same
      }
  }

  def busyState(waiter: ActorRef[Waiter.Command], conf: ChefConf): Behavior[Command] = Behaviors.receive {
    (ctx, msg) => {
      msg match {
        case FinishOrder(order) =>
          ctx.log.info(s"Order #${order.orderId} cooked.")
          waiter ! Waiter.DeliverOrder(CookedOrder(order.orderId, order.dishes))
          freeState(conf)
        case TakeOrder(order, _, replyTo) =>
          replyTo ! Busy
          ctx.log.info(s"Can't accept order #${order.orderId}. I'm still busy!")
          Behaviors.same
      }
    }
  }
}
