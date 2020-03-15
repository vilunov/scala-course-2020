package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import khinkali.Result.{Busy, Ok}

import scala.concurrent.duration._
//import scala.language.implicitConversions


object Chef {

  sealed trait ChefLogMessage

  implicit def msg2str(msg: ChefLogMessage): String = msg.toString

  case class AcceptedMessage(orderId: Int) extends ChefLogMessage {
    override def toString: String =
      s"Accepted order #${orderId}. Now I'm busy!"
  }

  case class FinishedMessage(orderId: Int) extends ChefLogMessage {
    override def toString: String =
      s"Order #${orderId} cooked."
  }

  case class BusyMessage(orderId: Int) extends ChefLogMessage {
    override def toString: String =
      s"Can't accept order #${orderId}. I'm still busy!"

  }

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
          ctx.log.info(AcceptedMessage(order.orderId))
          busyState(orderReceiver, conf)
        case _ => Behaviors.same
      }
  }

  def busyState(waiter: ActorRef[Waiter.Command], conf: ChefConf): Behavior[Command] = Behaviors.receive {
    (ctx, msg) => {
      msg match {
        case FinishOrder(order) =>
          ctx.log.info(FinishedMessage(order.orderId))
          waiter ! Waiter.DeliverOrder(CookedOrder(order.orderId, order.dishes))
          freeState(conf)
        case TakeOrder(order, _, replyTo) =>
          replyTo ! Busy
          ctx.log.info(BusyMessage(order.orderId))
          Behaviors.same
      }
    }
  }
}
