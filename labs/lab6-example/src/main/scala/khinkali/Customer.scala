package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Customer.{Command, Eat, Leave, LeaveOrder, Start}
import khinkali.Waiter.ReceiveOrder

import scala.concurrent.duration._
import scala.util.Random

case class Customer(waiter: ActorRef[Waiter.Command], order: CustomerOrder, random: Random,
                    minSelectingTime: Int, maxSelectingTime: Int,
                    minEatingTime: Int, maxEatingTime: Int) {

  def start(order: CustomerOrder, waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val selectingTime = minSelectingTime + random.nextInt((maxSelectingTime - minSelectingTime + 1))
          ctx.scheduleOnce(selectingTime.second, ctx.self, LeaveOrder(order))
          leaveOrder(waiter)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case LeaveOrder(order) =>
        ctx.log.info(s"Leaving order $order")
        waiter ! ReceiveOrder(order, ctx.self)
        waitForEat
      case _ => Behaviors.same
    }
  }

  def waitForEat: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat =>
        ctx.log.info(s"Now eating")
        val eatingTime = minEatingTime + random.nextInt((maxEatingTime - minEatingTime + 1))
        ctx.scheduleOnce(eatingTime.second, ctx.self, Leave)
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

object Customer {
  sealed trait Command

  case object Start extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command

  def apply(waiter: ActorRef[Waiter.Command], order: CustomerOrder, random: Random,
            minSelectingTime: Int, maxSelectingTime: Int,
            minEatingTime: Int, maxEatingTime: Int): Behavior[Command] =

    new Customer(waiter: ActorRef[Waiter.Command], order: CustomerOrder, random: Random,
                 minSelectingTime: Int, maxSelectingTime: Int,
                 minEatingTime: Int, maxEatingTime: Int)
      .start(order, waiter)
}
