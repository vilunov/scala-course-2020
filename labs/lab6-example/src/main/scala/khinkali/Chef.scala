package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._
import scala.util.Random

object Chef {

  sealed trait Command
  case class TakeOrder(order: Order, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  var rngesus: Random = _
  var avgCookingTime: Int = _
  var varCookingTime: Int = _

  def apply(rng: Random, cfg: CookingTime): Behavior[Command] = {
    rngesus = rng
    avgCookingTime = cfg.avg
    varCookingTime = cfg.varia
    waitForOrder
  }

  def waitForOrder: Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          ctx.log.info(s"Taking order ${order.orderId}")
          replyTo ! Result.Ok
          // У меня проблема с тем что Шеф по сути весь батч хинкалей готовит за один раз
          // Т.е. даже если там 100 разных вариаций порций, он всё равно всё разом бахнет и отдаст
          // Мне лень переделывать если честно так спать хочу госпаде
          // cooking time can vary from (avgCookingTime - 0.5 * varCookingTime) to (avgCookingTime + 0.5 * varCookingTime)
          val cookingTimeThisTime = avgCookingTime + math.round(varCookingTime * (rngesus.nextGaussian() - 0.5f))
          ctx.scheduleOnce(cookingTimeThisTime.second, ctx.self, FinishOrder(order.orderId, customer))
          cookOrder

        case _ => Behaviors.same // keep cooking

      }
    }

  def cookOrder: Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(_, replyTo, _) =>
          replyTo ! Result.Busy
          Behaviors.same

        case FinishOrder(orderId, customer) =>
          ctx.log.info(s"Finished Order $orderId")
          customer ! Customer.Eat
          waitForOrder

        case _ => Behaviors.same // keep cooking

      }
    }
}
