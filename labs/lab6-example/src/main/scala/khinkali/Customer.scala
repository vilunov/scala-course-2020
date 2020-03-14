package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Customer {
  implicit val timeout: Timeout = Timeout(1.second)
  sealed trait Command

  case object Start extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command
  case object Continue extends Command

  def apply(waiter: ActorRef[Waiter.Command],
            order: CustomerOrder,
            gen: ActorRef[RandomnessManager.Command],
            waitingRange: (Double, Double)): Behavior[Command] =
    start(order, waiter, gen, waitingRange)

  def start(order: CustomerOrder,
            waiter: ActorRef[Waiter.Command],
            gen: ActorRef[RandomnessManager.Command],
            waitingRange: (Double, Double)): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          ctx.ask(gen, RandomnessManager.Generate(waitingRange._1, waitingRange._2, _: ActorRef[Double])) {
            case Success(result) =>
              ctx.scheduleOnce(result.second, ctx.self, LeaveOrder(order))
              Continue
            case Failure(exception) =>
              ctx.log.error(exception.getMessage)
              Continue
          }
          leaveOrder(waiter, gen, waitingRange)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command],
                 gen: ActorRef[RandomnessManager.Command],
                 waitingRange: (Double, Double)): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case LeaveOrder(order) =>
          ctx.log.info(s"Leaving order $order")
          waiter ! Waiter.AcceptOrder(order, ctx.self)
          waitForEat(gen, waitingRange)
        case _ => Behaviors.same
      }
    }

  def waitForEat(gen: ActorRef[RandomnessManager.Command],
                 waitingRange: (Double, Double)): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Eat =>
          ctx.log.info(s"Now eating")
          ctx.ask(gen, RandomnessManager.Generate(waitingRange._1, waitingRange._2, _: ActorRef[Double])) {
            case Success(result) =>
              ctx.scheduleOnce(result.second, ctx.self, Leave)
              Continue
            case Failure(exception) =>
              ctx.log.error(exception.getMessage)
              Continue
          }
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
