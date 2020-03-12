package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Customer {
  sealed trait Command
  implicit val timeout: Timeout = Timeout(1.second)

  case object Proceed extends Command
  case object Start extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command

  def apply(waiter: ActorRef[Waiter.Command],
            order: CustomerOrder,
            gen: ActorRef[RandomGenerator.Command],
            cfg: CafeConfig): Behavior[Command] =
    start(order, waiter, gen, cfg)

  def start(order: CustomerOrder,
            waiter: ActorRef[Waiter.Command],
            gen: ActorRef[RandomGenerator.Command],
            cfg: CafeConfig): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val (waitFrom, waitTo) = cfg.waitingTime
          ctx.ask(gen, RandomGenerator.Generate(waitFrom, waitTo, _: ActorRef[Double])) {
            case Success(result) =>
              ctx.scheduleOnce(result.second, ctx.self, LeaveOrder(order))
              Proceed
            case Failure(exception) =>
              ctx.log.error(exception.getMessage)
              Proceed
          }
          leaveOrder(waiter, gen, cfg)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command],
                 gen: ActorRef[RandomGenerator.Command],
                 cfg: CafeConfig): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case LeaveOrder(order) =>
          ctx.log.info(s"Leaving order $order")
          waiter ! Waiter.AcceptOrder(order, ctx.self)
          waitForEat(gen, cfg)
        case _ => Behaviors.same
      }
    }

  def waitForEat(gen: ActorRef[RandomGenerator.Command],
                 cfg: CafeConfig): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Eat =>
          ctx.log.info(s"Now eating")
          val (waitFrom, waitTo) = cfg.eatingTime
          ctx.ask(gen, RandomGenerator.Generate(waitFrom, waitTo, _: ActorRef[Double])) {
            case Success(result) =>
              ctx.scheduleOnce(result.second, ctx.self, Leave)
              Proceed
            case Failure(exception) =>
              ctx.log.error(exception.getMessage)
              Proceed
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
