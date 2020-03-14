package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import khinkali.Result.{Busy, Ok}
import khinkali.Stuffing.{Beef, CheeseAndMushrooms, Mutton}
import khinkali.Waiter.ServeOrder

import scala.concurrent.duration._
import scala.util.Random

object Chef {

  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Result], waiter: ActorRef[Waiter.Command], tailChefs: Seq[ActorRef[Chef.Command]]) extends Command

  case class FinishOrder(orderId: Int, waiter: ActorRef[Waiter.Command]) extends Command

  def apply(config: Config, random: Random): Behavior[Command] =
    waitForOrderAndCook(config, random)

  def waitForOrderAndCook(config: Config, random: Random): Behavior[Command] = {
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, waiter, _) =>
          ctx.log.info(s"Now cooking ${order.orderId}")
          replyTo ! Ok
          val cookingTime = order.dishes.foldLeft(0D)((res: Double, order: Khinkali) => {
            res + (order.amount * timeForStuffing(order.stuffing, config, random))
          }).seconds
          ctx.scheduleOnce(cookingTime, ctx.self, FinishOrder(order.orderId, waiter))
          finishOrder(config, random)
        case _ => Behaviors.same
      }
    }
  }

  private def timeForStuffing(stuffing: Stuffing, config: Config, random: Random): Double = {
    stuffing match {
      case Beef => random.between(config.beefCookingMin, config.beefCookingMax)
      case Mutton => random.between(config.muttonCookingMin, config.muttonCookingMax)
      case CheeseAndMushrooms => random.between(config.cheeseAndMushroomsCookingMin, config.cheeseAndMushroomsCookingMax)
    }
  }

  def finishOrder(config: Config, random: Random): Behavior[Command] = {
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case FinishOrder(orderId, waiter) =>
          ctx.log.info(s"Finished cooking $orderId")
          waiter ! ServeOrder(orderId)
          waitForOrderAndCook(config, random)
        case TakeOrder(order, replyTo, _, tailChefs) =>
          ctx.log.info(s"Busy for ${order.orderId}")
          replyTo ! Busy(order, tailChefs)
          Behaviors.same
        case _ => Behaviors.same
      }
    }
  }
}
