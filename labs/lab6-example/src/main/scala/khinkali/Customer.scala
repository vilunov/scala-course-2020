package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object Customer {

  sealed trait Command

  case object Start extends Command

  case class LeaveOrder(order: CustomerOrder) extends Command

  case class Eat(cookedOrder: CookedOrder) extends Command

  case object Leave extends Command

  def apply(waiter: ActorRef[Waiter.Command], menu: Menu): Behavior[Command] =
    start(menu, waiter)

  def pickRandomDish(menu: Menu): CustomerOrder = // TODO: add parameters
    CustomerOrder(List(menu.menu(Random.between(0, menu.menu.length))(Random.between(1, 10))))

  def start(menu: Menu, waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val order = pickRandomDish(menu)
          ctx.scheduleOnce(1.second, ctx.self, LeaveOrder(order))
          leaveOrder(waiter)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case LeaveOrder(order) =>
        ctx.log.info(s"Leaving order $order")
        waiter ! Waiter.ReceiveOrder(order, ctx.self)
        waitForEat
      case _ => Behaviors.same
    }
  }

  def waitForEat: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat(CookedOrder(myId, food)) =>
        ctx.log.info(s"Now eating. Useless for me number on platter is $myId, food is $food")
        ctx.scheduleOnce(1.second, ctx.self, Leave)
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
