package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.duration._
import scala.util.Random

class Chef(val waiter: ActorRef[Waiter.Command], val config: ChefConfig) {

  def waitOrder(random: Random): Behavior[Chef.Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Chef.TakeOrder(order, replyTo) =>
          val orderId = order.orderId
          ctx.log.info(s"Chef starts cooking order $orderId")
          replyTo ! Result.Ok
          val time = Chef.computeTime(order, random, config)
          ctx.scheduleOnce(time, ctx.self, Chef.FinishOrder)
          cook(orderId, random)
        case _ => Behaviors.same
      }
    }

  def cook(orderId: Int, random: Random): Behavior[Chef.Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Chef.TakeOrder(_, replyTo) =>
          replyTo ! Result.Busy
          Behaviors.same
        case Chef.FinishOrder =>
          ctx.log.info(s"Chef finished cooking order $orderId")
          waiter ! Waiter.ServeOrder(orderId)
          waitOrder(random)
        case _ => Behaviors.same
      }
    }
}

object Chef {
  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Result]) extends Command
  object FinishOrder                                            extends Command

  def apply(
      waiter: ActorRef[Waiter.Command],
      seed: Long,
      config: ChefConfig
  ): Behavior[Command] =
    new Chef(waiter, config).waitOrder(new Random(seed))

  def computeTime(order: Order, random: Random, config: ChefConfig): FiniteDuration = {
    def computeStuffingTime(random: Random, range: TimeRange): Float =
      range.start + (range.end - range.start) * random.nextFloat()
    order.dishes
      .map(dish =>
        (1 to dish.amount)
          .map(_ =>
            dish.stuffing match {
              case Stuffing.Beef               => config.beef
              case Stuffing.Mutton             => config.mutton
              case Stuffing.CheeseAndMushrooms => config.cheeseAndMushrooms
            }
          )
          .map(computeStuffingTime(random, _))
          .sum
      )
      .sum
      .second
  }
}
