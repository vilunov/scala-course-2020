package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._
import scala.util.Random

object Chef {
  sealed trait Command

  case class TakeOrder(order: CustomerOrder, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(customer: ActorRef[Customer.Eat.type]) extends Command
  case class Cooking(customer: ActorRef[Customer.Eat.type], timeout:Float) extends Command

  val r = scala.util.Random

  def apply(waiter: ActorRef[Waiter.Command], cafeConfig: CafeConfig, r: Random.type): Behavior[Command] = {
    start(waiter,cafeConfig, r )
  }

  def start(waiter: ActorRef[Waiter.Command], cafeConfig: CafeConfig, r: Random.type):Behavior[Command]=
    Behaviors.receive { (cat, msg) =>
      msg match {
        case TakeOrder(order, replyTo, customer) =>
          cat.log.info(s"chef has taken an order for $customer")
          replyTo ! Result.Ok(order, customer, cat.self)
          val maxTime = cafeConfig.maxCookingTime
          val minTime = cafeConfig.minCookingTime
          val beef   = r.nextInt(maxTime-minTime+1)+minTime
          val mutton = r.nextInt(maxTime-minTime+1)+minTime
          val cheese = r.nextInt(maxTime-minTime+1)+minTime
          val overalTime = beef*order.dishes(0).amount+mutton*order.dishes(1).amount+cheese*order.dishes(2).amount
          cat.scheduleOnce(overalTime.millisecond, cat.self, FinishOrder(customer))
          finishOrder(waiter, cafeConfig, r)

        case _ => Behaviors.same
      }
    }


  def finishOrder(waiter: ActorRef[Waiter.Command], cafeConfig: CafeConfig, r: Random.type):Behavior[Command]=
    Behaviors.receive{ (cat, msg) =>
      msg match {
        case FinishOrder(cust) =>
          waiter ! Waiter.FinishOrder(cust)
          cat.log.info(s"chef has finished an order for $cust ")
          start(waiter, cafeConfig, r)
        case TakeOrder(order, replyTo, cust) =>
          cat.log.info(s"${cat.self} is busy ")
          replyTo ! Result.Busy(order, cust)
          Behaviors.same
        case _ => Behaviors.same
      }

  }

}
