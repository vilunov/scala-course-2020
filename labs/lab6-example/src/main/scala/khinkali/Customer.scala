package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._

object Customer {
  sealed trait Command

  case object Start                           extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat                             extends Command
  case object Leave                           extends Command

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(
      waiter: ActorRef[Waiter.Command],
      customerConfig: CustomerConfig,
      seed: Long
  ): Behavior[Command] = {
    val random: Random = new Random(seed)
    start(customerConfig, waiter, random)
  }

  def start(
      customerConfig: CustomerConfig,
      waiter: ActorRef[Waiter.Command],
      random: Random
  ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          ctx.scheduleOnce(
            customerConfig.orderDecisionTimeBoundaries.randomWithin(random).second,
            ctx.self,
            LeaveOrder(
              CustomerOrder.generateOrder(
                random,
                customerConfig
              )
            )
          )
          leaveOrder(customerConfig, waiter, random)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(customerConfig: CustomerConfig, waiter: ActorRef[Waiter.Command], random: Random): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case LeaveOrder(order) =>
          ctx.log.info(s"Leaving order $order")
          waiter ! Waiter.AcceptOrder(order, ctx.self)
          waitForEat(customerConfig, random)
        case _ => Behaviors.same
      }
    }

  def waitForEat(customerConfig: CustomerConfig, random: Random): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat =>
        ctx.log.info(s"Now eating")
        ctx.scheduleOnce(customerConfig.eatingTimeBoundaries.randomWithin(random).second, ctx.self, Leave)
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
