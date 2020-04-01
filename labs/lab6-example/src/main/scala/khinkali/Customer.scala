package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._
import scala.util.Random

object Customer {
  sealed trait Command

  case class Start(cafe: ActorRef[Cafe.Callback.type]) extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command

  def apply(waiter: ActorRef[Waiter.Command],
            order: CustomerOrder,
            cfg: Config,
            rng: Random): Behavior[Command] =
    start(order, waiter, cfg, rng)

  def start(order: CustomerOrder,
            waiter: ActorRef[Waiter.Command],
            cfg: Config,
            rng: Random): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start(value) =>
          val range = cfg.customers.order
          ctx.scheduleOnce(
            rng.between(range.from, range.to).seconds,
            ctx.self,
            LeaveOrder(order)
          )
          leaveOrder(waiter, value, cfg, rng)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command],
                 cafe: ActorRef[Cafe.Callback.type],
                 cfg: Config,
                 rng: Random): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case LeaveOrder(order) =>
          ctx.log.info(s"Leaving order $order")
          waiter ! Waiter.Order(order, ctx.self)
          waitForEat(cafe, cfg, rng)
        case _ => Behaviors.same
      }
    }

  def waitForEat(cafe: ActorRef[Cafe.Callback.type],
                 cfg: Config,
                 rng: Random): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Eat =>
          ctx.log.info(s"Now eating")
          val range = cfg.customers.eat
          ctx.scheduleOnce(
            rng.between(range.from, range.to).seconds,
            ctx.self,
            Leave
          )
          waitToLeave(cafe)
        case _ => Behaviors.same
      }
    }

  def waitToLeave(cafe: ActorRef[Cafe.Callback.type]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Leave =>
          ctx.log.info(s"Now leaving")
          cafe ! Cafe.Callback
          Behaviors.stopped
        case _ => Behaviors.same
      }
    }

}
