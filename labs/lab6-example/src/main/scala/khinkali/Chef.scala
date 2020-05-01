package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._
import scala.util.Random

object Chef {
  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  def apply(waiter: ActorRef[Waiter.Command], chefConf: ChefConf, seed: Int): Behavior[Command] = {
    val chefRand = new Random(seed)

    waitForOrder(waiter, chefRand, chefConf)
  }

  def waitForOrder(waiter: ActorRef[Waiter.Command], rand: Random, chefConf: ChefConf): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          val timeToCook = order.calculatePreparationTime(rand, chefConf.cookingTimeRange)

          ctx.log.info(s"Chef takes the order {} for customer {}", order.orderId, customer)
          ctx.scheduleOnce(timeToCook.second, ctx.self, FinishOrder(order.orderId, customer))
          replyTo ! Result.Ok

          finishOrder(waiter, rand, chefConf)
        case _ => Behaviors.same
      }
    }

  def finishOrder(waiter: ActorRef[Waiter.Command], rand: Random, chefConf: ChefConf): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case FinishOrder(orderId, customer) =>
          ctx.log.info(s"Order {} is ready for customer {}", orderId, customer)
          waiter ! Waiter.ServeOrder(customer)

          waitForOrder(waiter, rand, chefConf)
        case TakeOrder(_, replyTo, _) =>
          replyTo ! Result.Busy

          Behaviors.same
        case _ => Behaviors.same
      }
  }
}
