package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Chef {
  implicit val timeout: Timeout = Timeout(1.second)

  sealed trait Command

  object Waiting extends Command
  case class Cooking(order: Order, customer: ActorRef[Customer.Eat.type]) extends Command
  case class AcceptOrder(order: Order, customer: ActorRef[Customer.Eat.type], replyTo: ActorRef[Result]) extends Command
  case class GiveOutOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command
  object Continue extends Command

  def apply(cookingTimes: List[CookingTimes], gen: ActorRef[RandomnessManager.Command]): Behavior[Command] =
    handleOrder(cookingTimes, gen)

  def handleOrder(cookingTimes: List[CookingTimes], gen: ActorRef[RandomnessManager.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case AcceptOrder(order, customer, replyTo) =>
          ctx.log.info(s"Chef accepted order ${order.orderId}")
          replyTo ! Result.Ok
          ctx.scheduleOnce(1.second, ctx.self, Cooking(order, customer))
          cookOrder(cookingTimes, gen)
        case _ => Behaviors.same
      }
    }

  def cookOrder(cookingTimes: List[CookingTimes], gen: ActorRef[RandomnessManager.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Cooking(order, customer) =>
          ctx.log.info(s"Cooking order ${order.orderId}")
          order.dishes.headOption match {
            case Some(value) =>
              val (from, to) = value.stuffing match {
                case Stuffing.Beef =>
                  val BeefTime(f, t) = cookingTimes.findLast(_.isInstanceOf[BeefTime]).get
                  (f, t)
                case Stuffing.Chicken =>
                  val ChickenTime(f, t) = cookingTimes.findLast(_.isInstanceOf[ChickenTime]).get
                  (f, t)
                case Stuffing.Vegan =>
                  val VeganTime(f, t) = cookingTimes.findLast(_.isInstanceOf[VeganTime]).get
                  (f, t)
              }

              ctx.ask(gen, RandomnessManager.Generate(from, to, _: ActorRef[Double])) {
                case Success(result) =>
                  ctx.scheduleOnce((value.amount * result).second, ctx.self,
                    Cooking(Order(order.orderId, order.dishes.tail), customer))
                  Continue
                case Failure(exception) =>
                  ctx.log.error(exception.getMessage)
                  Continue
              }
              Behaviors.same

            case None =>
              ctx.scheduleOnce(0.1.second, ctx.self, GiveOutOrder(order.orderId, customer))
              giveOut(cookingTimes, gen)
          }
        case _ => Behaviors.same
      }
    }

  def giveOut(cookingTimes: List[CookingTimes], gen: ActorRef[RandomnessManager.Command]): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case GiveOutOrder(orderId, customer) =>
          ctx.log.info(s"Order $orderId was given out")
          customer ! Customer.Eat
          handleOrder(cookingTimes, gen)
        case _ => Behaviors.same
      }
    }
}
