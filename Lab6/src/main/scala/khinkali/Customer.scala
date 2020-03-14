package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Waiter.TakeOrder

import scala.concurrent.duration._
import scala.util.Random

object Customer {

  sealed trait Command

  case object Start extends Command

  case class LeaveOrder(order: CustomerOrder) extends Command

  case object Eat extends Command

  case object Leave extends Command

  def apply(config: Config, random: Random, waiter: ActorRef[Waiter.Command], order: CustomerOrder): Behavior[Command] =
    start(config, random, order, waiter)

  def start(config: Config, random: Random, order: CustomerOrder, waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          ctx.log.info(s"Thinking")
          val orderingTime = random.between(config.customerOrderingMin, config.customerOrderingMax).seconds
          ctx.scheduleOnce(orderingTime, ctx.self, LeaveOrder(order))
          leaveOrder(config, random, waiter)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(config: Config, random: Random, waiter: ActorRef[Waiter.Command]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case LeaveOrder(order) =>
        ctx.log.info(s"Leaving order $order")
        waiter ! TakeOrder(order, ctx.self)
        waitForEat(config, random)
      case _ => Behaviors.same
    }
  }

  def waitForEat(config: Config, random: Random): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat =>
        ctx.log.info(s"Now eating")
        val eatingTime = random.between(config.customerEatingMin, config.customerEatingMax).seconds
        ctx.scheduleOnce(eatingTime, ctx.self, Leave)
        waitToLeave
      case _ => Behaviors.same
    }
  }

  def waitToLeave: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Leave =>
        ctx.log.info(s"Now leaving")
        Behaviors.stopped
      case _ => Behaviors.same
    }
  }

}
