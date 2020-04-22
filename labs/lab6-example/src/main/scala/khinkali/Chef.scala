package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._
import scala.util.Random

object Chef {
  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(orderId: Int, customer: ActorRef[Customer.Eat.type])                          extends Command

  val random: Random = new Random()

  def apply(waiter: ActorRef[Waiter.Command], chefConfig: ChefConfig, seed: Long): Behavior[Command] = {
    random.setSeed(seed)
    waitForOrder(waiter, chefConfig)
  }

  def waitForOrder(waiter: ActorRef[Waiter.Command], chefConfig: ChefConfig): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          ctx.log.info(s"taking order ${order.orderId}")
          replyTo ! Result.Ok
          ctx.scheduleOnce(
            order.preparationTime(random, chefConfig.cookingTimeBoundaries).second,
            ctx.self,
            FinishOrder(order.orderId, customer)
          )
          cook(waiter, chefConfig)
        case _ => Behaviors.same
      }
  }

  def cook(waiter: ActorRef[Waiter.Command], chefConfig: ChefConfig): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case TakeOrder(_, replyTo, _) =>
          replyTo ! Result.Busy
          Behaviors.same
        case FinishOrder(orderId, customer) =>
          ctx.log.info(s"finished cooking $orderId")
          waiter ! Waiter.ServeOrder(orderId, customer)
          waitForOrder(waiter, chefConfig)
        case _ => Behaviors.same
      }
  }
}
