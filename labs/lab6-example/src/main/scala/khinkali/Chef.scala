package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._

object Chef {

  sealed trait Command

  case class TakeOrder(order: Order,
                       replyToNow: ActorRef[Result],
                       replyToLater: ActorRef[Waiter.Command],
                       customer: ActorRef[Customer.Eat.type])
      extends Command

  case class Cook(order: Order,
                  waiter: ActorRef[Waiter.Command],
                  customer: ActorRef[Customer.Eat.type])
      extends Command

  case class Finish(order: Order,
                    waiter: ActorRef[Waiter.Command],
                    customer: ActorRef[Customer.Eat.type])
      extends Command

  def calculateDelay(order: Order): Double = {
    order.dishes.foldLeft(0.0) { (total, pos) =>
      val (from, to) = pos.stuffing match {
        case Stuffing.Beef               => Config.chefConfig.beef
        case Stuffing.Mutton             => Config.chefConfig.mutton
        case Stuffing.CheeseAndMushrooms => Config.chefConfig.cheese
      }
      total + Config.rng.between(from, to) * pos.amount
    }
  }

  def apply(): Behavior[Command] = {
    waitForOrder
  }

  def waitForOrder: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case TakeOrder(order, replyToNow, replyToLater, customer) =>
        val id = order.orderId
        ctx.log.info(s"Taken order#$id")
        replyToNow ! Result.Ok
        ctx.self ! Cook(order, replyToLater, customer)
        cook
      case Cook(order, waiter, customer) =>
        new Exception("Received COOK message while waiting for orders")
        Behaviors.same
      case Finish(order, waiter, customer) =>
        new Exception("Received FINISH message while waiting for orders")
        Behaviors.same
    }
  }

  def cook: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Cook(order, waiter, customer) =>
        val delay: Double = calculateDelay(order)
        ctx.log.info(s"delay = $delay")
        ctx.scheduleOnce(
          delay.seconds,
          ctx.self,
          Finish(order, waiter, customer)
        )
        deliver
      case TakeOrder(_, replyToNow, _, _) =>
        replyToNow ! Result.Busy
        Behaviors.same
      case Finish(order, waiter, customer) =>
        new Exception("Received FINISH message while starting cooking an order")
        Behaviors.same
    }
  }

  def deliver: Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case TakeOrder(_, replyToNow, _, _) =>
        replyToNow ! Result.Busy
        Behaviors.same
      case Cook(order, waiter, customer) =>
        new Exception("Received COOK message while finishing the order")
        Behaviors.same
      case Finish(order, waiter, customer) =>
        val id = order.orderId
        ctx.log.info(s"Finished order#$id")
        waiter ! Waiter.Serve(customer)
        waitForOrder
    }
  }

}
