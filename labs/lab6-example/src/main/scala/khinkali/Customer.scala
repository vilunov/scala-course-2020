package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Cafe.CustomerTerminated
import khinkali.Waiter.TakeOrder

import scala.concurrent.duration._
import scala.util.Random

object Customer {

  sealed trait Command

  case object Start extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command

  var rngesus: Random = _
  var avgSelectingTime: Int = _
  var varSelectingTime: Int = _
  var avgEatingTime: Int = _
  var varEatingTime: Int = _

  var cafe: ActorRef[Cafe.CustomerTerminated.type] = _

  def apply(cafe: ActorRef[Cafe.CustomerTerminated.type], waiter: ActorRef[Waiter.Command],
            order: CustomerOrder,
            rng: Random, stcfg: SelectingTime, etcfg: EatingTime): Behavior[Command] = {
    // я знаю что это надо сделать отдельным классом с конструктором но мне так лень если честно 2 часа ночи
    this.cafe = cafe
    rngesus = rng
    avgSelectingTime = stcfg.avg
    varSelectingTime = stcfg.varia
    avgEatingTime = etcfg.avg
    varEatingTime = etcfg.varia
    start(order, waiter)
  }

  def start(order: CustomerOrder, waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val selectingTimeThisTime = avgSelectingTime + math.round(varSelectingTime * (rngesus.nextGaussian() - 0.5f))
          ctx.scheduleOnce(selectingTimeThisTime.second, ctx.self, LeaveOrder(order))
          leaveOrder(waiter)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case LeaveOrder(order) =>
        ctx.log.info(s"Leaving order $order")
        waiter ! TakeOrder(ctx.self, order)
        waitForEat
      case _ => Behaviors.same
    }
  }

  def waitForEat: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat =>
        ctx.log.info(s"Now eating")
        val eatingTimeThisTime = avgEatingTime + math.round(varEatingTime * (rngesus.nextGaussian() - 0.5f))
        ctx.scheduleOnce(eatingTimeThisTime.second, ctx.self, Leave)
        waitToLeave
      case _ => Behaviors.same
    }
  }

  def waitToLeave: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Leave =>
        ctx.log.info(s"Now leaving")
        cafe ! CustomerTerminated
        Behaviors.stopped
      case _ => Behaviors.same
    }
  }

}
