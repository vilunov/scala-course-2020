package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._
import scala.util.Random

object Customer {
  sealed trait Command

  case object Start extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command

  def apply(waiter: ActorRef[Waiter.Command], seed: Int, customerConf: CustomerConf): Behavior[Command] = {
    val customerRand: Random = new Random(seed)

    start(waiter, customerRand, customerConf)
  }

  def start(waiter: ActorRef[Waiter.Command], rand: Random, customerConf: CustomerConf): Behavior[Command] = {
    Behaviors.receive { (ctx, msg) =>
        msg match {
          case Start =>
            val order = CustomerOrder.generateOrder(rand)
            val timeToOrder = customerConf.decisionTimeRange.inRange(rand).second

            ctx.log.info(s"Deciding what to order...")
            ctx.scheduleOnce(timeToOrder, ctx.self, LeaveOrder(order))

            leaveOrder(waiter, rand, customerConf)
          case _ => Behaviors.same
        }
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command], rand: Random, customerConf: CustomerConf): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case LeaveOrder(order) =>
        ctx.log.info(s"Leaving order $order")
        waiter ! Waiter.TakeOrder(order, ctx.self)

        waitForEat(rand, customerConf)
      case _ => Behaviors.same
    }
  }

  def waitForEat(rand: Random, customerConf: CustomerConf): Behavior[Command] = {
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Eat =>
          val timeToEat = customerConf.eatingTimeRange.inRange(rand).second

          ctx.log.info(s"Now eating")
          ctx.scheduleOnce(timeToEat, ctx.self, Leave)

          waitToLeave
        case _ => Behaviors.same
      }
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
