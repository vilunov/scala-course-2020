package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._
import scala.util.Random

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

  def calculateDelay(order: Order, cfg: Config, rng: Random): Double = {
    order.dishes.foldLeft(0.0) { (total, pos) =>
      val range = pos.stuffing match {
        case Stuffing.Beef               => cfg.chefs.beef
        case Stuffing.Mutton             => cfg.chefs.mutton
        case Stuffing.CheeseAndMushrooms => cfg.chefs.cheese
      }
      total + rng.between(range.from, range.to) * pos.amount
    }
  }

  def apply(cfg: Config, rng: Random): Behavior[Command] = {
    waitForOrder(cfg, rng)
  }

  def waitForOrder(cfg: Config, rng: Random): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, replyToNow, replyToLater, customer) =>
          val id = order.orderId
          ctx.log.info(s"Taken order#$id")
          replyToNow ! Result.Ok
          ctx.self ! Cook(order, replyToLater, customer)
          cook(cfg, rng)
        case Cook(order, waiter, customer) =>
          throw new Exception("Received COOK message while waiting for orders")
          ctx.log.error("Received COOK message while waiting for orders")
          Behaviors.same
        case Finish(order, waiter, customer) =>
          throw new Exception(
            "Received FINISH message while waiting for orders"
          )
          ctx.log.error("Received FINISH message while waiting for orders")
          Behaviors.same
      }
    }

  def cook(cfg: Config, rng: Random): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case Cook(order, waiter, customer) =>
          val delay: Double = calculateDelay(order, cfg, rng)
          ctx.log.info(s"delay = $delay")
          ctx.scheduleOnce(
            delay.seconds,
            ctx.self,
            Finish(order, waiter, customer)
          )
          deliver(cfg, rng)
        case TakeOrder(_, replyToNow, _, _) =>
          replyToNow ! Result.Busy
          Behaviors.same
        case Finish(order, waiter, customer) =>
          throw new Exception(
            "Received FINISH message while starting cooking an order"
          )
          ctx.log.error(
            "Received FINISH message while starting cooking an order"
          )
          Behaviors.same
      }
  }

  def deliver(cfg: Config, rng: Random): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case TakeOrder(_, replyToNow, _, _) =>
          replyToNow ! Result.Busy
          Behaviors.same
        case Cook(order, waiter, customer) =>
          throw new Exception("Received COOK message while finishing the order")
          ctx.log.error("Received COOK message while finishing the order")
          Behaviors.same
        case Finish(order, waiter, customer) =>
          val id = order.orderId
          ctx.log.info(s"Finished order#$id")
          waiter ! Waiter.Serve(customer)
          waitForOrder(cfg, rng)
      }
  }

}
