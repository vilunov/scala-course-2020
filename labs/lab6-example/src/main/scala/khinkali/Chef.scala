package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._
import scala.util.Random

object Chef {

  sealed trait Command
  case class TakeOrder(order: Order, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  def apply(seed: Int, cfg: CookingTime): Behavior[Command] = {
    val rngesus: Random = new Random(seed)
    waitForOrder(rngesus, cfg)
  }

  def waitForOrder(rngesus: Random, cfg: CookingTime): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          ctx.log.info(s"Taking order ${order.orderId}")
          replyTo ! Result.Ok
          // cooking time can vary from (avgCookingTime - 0.5 * varCookingTime) to (avgCookingTime + 0.5 * varCookingTime)
          val cookingTimeThisTime = cfg.avg + math.round(cfg.varia * (rngesus.nextFloat() - 0.5f))
          ctx.scheduleOnce(cookingTimeThisTime.second, ctx.self, FinishOrder(order.orderId, customer))
          cookOrder(rngesus, cfg)

        case _ => Behaviors.same // keep cooking

      }
    }

  def cookOrder(rngesus: Random, cfg: CookingTime): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(_, replyTo, _) =>
          replyTo ! Result.Busy
          Behaviors.same

        case FinishOrder(orderId, customer) =>
          ctx.log.info(s"Finished Order $orderId")
          customer ! Customer.Eat
          waitForOrder(rngesus, cfg)

        case _ => Behaviors.same // keep cooking

      }
    }
}
