package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._

object Customer {

  sealed trait Command

  case class Start(conf: CustomerConf) extends Command

  case class LeaveOrder(order: CustomerOrder, eatTimePicker: () => Double) extends Command

  case class Eat(cookedOrder: CookedOrder) extends Command

  case object Leave extends Command

  def apply(waiter: ActorRef[Waiter.Command], menu: Menu): Behavior[Command] =
    start(menu, waiter)

  def pickFood(menu: Menu, conf: CustomerConf): CustomerOrder = {
    val dishesNum = Math.min(menu.menu.length, conf.khinkaliRange.length)
    CustomerOrder((0 until dishesNum).map { i => menu(i)(MyRandom.between(conf.khinkaliRange(i))) }.toList)
  }

  def start(menu: Menu, waiter: ActorRef[Waiter.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start(conf) =>
          val order = pickFood(menu, conf)
          ctx.log.info(s"Leaving order $order")
          waiter ! Waiter.ReceiveOrder(order, ctx.self)
          waitForEat(conf)
        case _ => Behaviors.same
      }
    }

  def waitForEat(conf: CustomerConf): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat(CookedOrder(myId, food)) =>
        ctx.log.info(s"Now eating. Useless for me number on platter is $myId, food is $food")
        ctx.scheduleOnce(MyRandom.between(conf.eatDelayRange).second, ctx.self, Leave)
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
