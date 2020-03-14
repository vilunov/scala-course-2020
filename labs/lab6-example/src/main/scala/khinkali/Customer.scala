package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Cafe.CustomerTerminated
import khinkali.Customer._
import khinkali.Waiter.TakeOrder

import scala.concurrent.duration._
import scala.util.Random

class Customer(
  val waiter: ActorRef[Waiter.Command],
  val cafe: ActorRef[Cafe.CustomerTerminated.type],
  val rngesus: Random,
  val config: CustomerConfig) {

  def start(order: CustomerOrder): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val selectingTimeThisTime = config.selectingTime.avg
            + math.round(config.selectingTime.varia
            * (rngesus.nextGaussian() - 0.5f))
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
        val eatingTimeThisTime = config.eatingTime.avg + math.round(config.eatingTime.avg * (rngesus.nextFloat() - 0.5f))
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

object Customer {

  sealed trait Command

  case object Start extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command


  def apply(seed: Int, waiter: ActorRef[Waiter.Command], cafe: ActorRef[Cafe.CustomerTerminated.type],
            config: CustomerConfig, orderConfig: OrderConfig): Behavior[Command] = {

    val rngesus = new Random(seed)
    // generate an order
    val nDishes = orderConfig.orderedDishes.avg
      + math.round(orderConfig.orderedDishes.varia
      * (rngesus.nextFloat() - 0.5f)).toInt

    val order = (1 to nDishes).map {
      _ => {
        val stuffing = rngesus.nextInt().abs % 3 match {
          case 0 => Stuffing.Beef
          case 1 => Stuffing.CheeseAndMushrooms
          case 2 => Stuffing.Mutton
        }
        val amount = orderConfig.khinkalisInDish.avg
          + math.round(orderConfig.khinkalisInDish.varia * (rngesus.nextFloat() - 0.5f))

        Khinkali(stuffing, amount)
      }
    }.toList

    // start a customer
    new Customer(waiter, cafe, rngesus, config).start(CustomerOrder(order))
  }


}
