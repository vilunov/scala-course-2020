package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Customer.Eat

import scala.concurrent.duration._
import scala.util.Random

case class Chef(random: Random, minCookingTime: Int, maxCookingTime: Int){
  import Chef._

  def start: Behavior[Chef.Command] = waitForOrder

  def waitForOrder: Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          ctx.log.info(s"cooking order ${order.orderId}")
          replyTo ! Result.Ok

          val cookingTime = minCookingTime + random.nextInt(maxCookingTime - minCookingTime + 1)
          ctx.scheduleOnce(cookingTime.second, ctx.self, FinishOrder(order, customer))
          cookOrder

        case _ => Behaviors.same
      }
    }

  def cookOrder: Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          replyTo ! Result.Busy
          Behaviors.same

        case FinishOrder(order, customer) =>
          ctx.log.info(s"finishing order $order")
          customer ! Eat
          waitForOrder

        case _ => Behaviors.same

      }
    }
}


object Chef {
  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(order: Order, customer: ActorRef[Customer.Eat.type]) extends Command

  def apply(random: Random, minCoookingTime: Int, maxCookingTime: Int): Behavior[Command] =
    new Chef(random: Random, minCoookingTime: Int, maxCookingTime: Int).start
}
