package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Stuffing.Beef

import scala.concurrent.duration._
import scala.util.Random

object Customer {
  sealed trait Command

  case object Start                           extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat                             extends Command
  case object Leave                           extends Command

  def decide(khinkaliRange: Range, random: Random, stuffingDuration: Map[Stuffing, FiniteDuration]): CustomerOrder =
    CustomerOrder(
      List.from(
        (1 to khinkaliRange.toRandomInt(random)).map(i =>
          Khinkali(Stuffing.getRandom(random), khinkaliRange.toRandomInt(random), stuffingDuration)
        )
      )
    )

  def apply(
      waiter: ActorRef[Waiter.Command],
      order: CustomerOrder,
      decisionTime: FiniteDuration,
      eatingTime: FiniteDuration,
      cafe: ActorRef[Cafe.CustomerLeft.type]
  ): Behavior[Command] =
    start(order, waiter, decisionTime, eatingTime, cafe)

  def start(
      order: CustomerOrder,
      waiter: ActorRef[Waiter.Command],
      decisionTime: FiniteDuration,
      eatingTime: FiniteDuration,
      cafe: ActorRef[Cafe.CustomerLeft.type]
  ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          ctx.scheduleOnce(decisionTime, ctx.self, LeaveOrder(order))
          leaveOrder(waiter, eatingTime, cafe)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(
      waiter: ActorRef[Waiter.Command],
      eatingTime: FiniteDuration,
      cafe: ActorRef[Cafe.CustomerLeft.type]
  ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case LeaveOrder(order) =>
          ctx.log.info(s"Leaving order $order")
          waiter ! Waiter.ReceiveOrder(order, ctx.self)
          waitForEat(eatingTime, cafe)
        case _ => Behaviors.same
      }
    }

  def waitForEat(eatingTime: FiniteDuration, cafe: ActorRef[Cafe.CustomerLeft.type]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Eat =>
          ctx.log.info(s"Now eating")
          ctx.scheduleOnce(eatingTime, ctx.self, Leave)
          waitToLeave(cafe)
        case _ => Behaviors.same
      }
    }

  def waitToLeave(cafe: ActorRef[Cafe.CustomerLeft.type]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Leave =>
        ctx.log.info(s"Now leaving")
        cafe ! Cafe.CustomerLeft
        Behaviors.stopped
      case _ => Behaviors.same
    }
  }

}
