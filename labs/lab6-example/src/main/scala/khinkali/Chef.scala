package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Chef {
  implicit val timeout: Timeout = Timeout(1.second)

  sealed trait Command
  object Proceed extends Command
  case class Cooking(order: Order, customer: ActorRef[Customer.Eat.type]) extends Command
  case class CookDishes(order: Order, customer: ActorRef[Customer.Eat.type]) extends Command
  case class TakeOrder(order: Order, customer: ActorRef[Customer.Eat.type], replyTo: ActorRef[Result]) extends Command
  case class FinishOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  def apply(cookingTimes: List[CookingTime], gen: ActorRef[RandomGenerator.Command]): Behavior[Command] =
    acceptOrder(cookingTimes, gen)

  def acceptOrder(cookingTimes: List[CookingTime], gen: ActorRef[RandomGenerator.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(order, customer, replyTo) =>
          ctx.log.info(s"Chef accepted order ${order.orderId}")
          replyTo ! Result.Ok
          ctx.self ! Cooking(order, customer)
          cookOrder(cookingTimes, gen)
        case _ => Behaviors.same
      }
    }

  def cookOrder(cookingTimes: List[CookingTime], gen: ActorRef[RandomGenerator.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Cooking(order, customer) =>
          ctx.log.info(s"Cooking order ${order.orderId}")
          ctx.self ! CookDishes(order, customer)
          Behaviors.same

        case CookDishes(order, customer) =>
          order.dishes.headOption match {
            case Some(value) =>
              val (from, to) = value.stuffing match {
                case Stuffing.Beef =>
                  val BeefTime(f, t) = cookingTimes.findLast(_.isInstanceOf[BeefTime]).get
                  (f, t)
                case Stuffing.Mutton =>
                  val MuttonTime(f, t) = cookingTimes.findLast(_.isInstanceOf[MuttonTime]).get
                  (f, t)
                case Stuffing.CheeseAndMushrooms =>
                  val CheeseAndMushroomsTime(f, t) = cookingTimes.findLast(_.isInstanceOf[CheeseAndMushroomsTime]).get
                  (f, t)
              }

              ctx.ask(gen, RandomGenerator.Generate(from, to, _: ActorRef[Double])) {
                case Success(result) =>
                  ctx.scheduleOnce((value.amount * result).second, ctx.self,
                    CookDishes(Order(order.orderId, order.dishes.tail), customer))
                  Proceed
                case Failure(exception) =>
                  ctx.log.error(exception.getMessage)
                  Proceed
              }
              Behaviors.same

            case None =>
              ctx.self ! FinishOrder(order.orderId, customer)
              giveOut(cookingTimes, gen)
          }

        case TakeOrder(_, _, replyTo) =>
          replyTo ! Result.Busy
          Behaviors.same

        case _ => Behaviors.same
      }
    }

  def giveOut(cookingTimes: List[CookingTime], gen: ActorRef[RandomGenerator.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case FinishOrder(orderId, customer) =>
          ctx.log.info(s"Order $orderId was cooked and given out")
          customer ! Customer.Eat
          acceptOrder(cookingTimes, gen)
        case TakeOrder(_, _, replyTo) =>
          replyTo ! Result.Busy
          Behaviors.same
        case _ => Behaviors.same
      }
    }

}
