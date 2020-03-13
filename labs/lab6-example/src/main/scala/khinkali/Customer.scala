package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.duration._
import scala.util.Random

class Customer(val waiter: ActorRef[Waiter.Command], val config: CustomerConfig) {

  def start(random: Random): Behavior[Customer.Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Customer.Start =>
          val order = Customer.generateOrder(random, config)
          val time  = Utils.randomRange(random, config.orderingTime)
          ctx.scheduleOnce(time.second, ctx.self, Customer.LeaveOrder(order))
          leaveOrder(random)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(random: Random): Behavior[Customer.Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Customer.LeaveOrder(order) =>
          ctx.log.info(s"Leaving order $order")
          waiter ! Waiter.TakeOrder(ctx.self, order)
          waitForEat(random)
        case _ => Behaviors.same
      }
    }

  def waitForEat(random: Random): Behavior[Customer.Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Customer.Eat =>
          ctx.log.info(s"Now eating")
          val time = Utils.randomRange(random, config.eatingTime)
          ctx.scheduleOnce(time.second, ctx.self, Customer.Leave)
          waitToLeave
        case _ => Behaviors.same
      }
    }

  def waitToLeave: Behavior[Customer.Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Customer.Leave =>
          ctx.log.info(s"Now leaving")
          Behaviors.stopped
        case _ => Behaviors.same
      }
    }
}

object Customer {
  sealed trait Command

  case object Start                           extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat                             extends Command
  case object Leave                           extends Command

  def apply(
      waiter: ActorRef[Waiter.Command],
      seed: Long,
      config: CustomerConfig
  ): Behavior[Command] =
    new Customer(waiter, config).start(new Random(seed))

  def generateOrder(random: Random, config: CustomerConfig): CustomerOrder = {
    val diff      = config.numDishes.end - config.numDishes.start + 1
    val numDishes = config.numDishes.start + random.nextInt(diff)
    CustomerOrder(
      (1 to numDishes)
        .map(_ => {
          val diff       = config.dishAmount.end - config.dishAmount.start + 1
          val dishAmount = config.dishAmount.start + random.nextInt(diff)
          val stuff      = random.nextInt(3)
          stuff match {
            case 0 => Khinkali(Stuffing.Beef, dishAmount)
            case 1 => Khinkali(Stuffing.Mutton, dishAmount)
            case _ => Khinkali(Stuffing.CheeseAndMushrooms, dishAmount)
          }
        })
        .toList
    )
  }

}
