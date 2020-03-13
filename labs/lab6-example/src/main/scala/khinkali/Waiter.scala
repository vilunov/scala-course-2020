package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.Success
import scala.concurrent.duration._

object Waiter {
  sealed trait Command

  case class Start(chefs: List[ActorRef[Chef.Command]])                            extends Command
  case class TakeOrder(customer: ActorRef[Customer.Command], order: CustomerOrder) extends Command
  case class AssignChef(
      customer: ActorRef[Customer.Command],
      order: Order,
      left_chefs: List[ActorRef[Chef.Command]]
  ) extends Command
  case class ChefAssigned(orderId: Int) extends Command
  case class ServeOrder(orderId: Int)   extends Command
  implicit val timeout: Timeout = Timeout(1.second)

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {
        case Start(chefs) => serve(chefs, Map())
        case _            => Behaviors.same
      }
    }

  def serve(
      chefs: List[ActorRef[Chef.Command]],
      customers: Map[Int, ActorRef[Customer.Command]]
  ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(customer, customer_order) =>
          val order = customer_order.toOrder(customers.toList.length)
          ctx.log.info(s"Waiter takes order $customer_order, orderId ${order.orderId}")
          ctx.self ! AssignChef(customer, order, chefs)
          serve(chefs, customers.updated(order.orderId, customer))
        case AssignChef(customer, order, left_chefs) =>
          left_chefs match {
            case Nil =>
              ctx.log.info(s"All chefs are busy.")
              ctx.scheduleOnce(1.second, ctx.self, AssignChef(customer, order, chefs))
              Behaviors.same
            case head :: tail =>
              ctx.ask[Chef.TakeOrder, Result](head, Chef.TakeOrder(order, _)) {
                case Success(Result.Ok) => ChefAssigned(order.orderId)
                case _                  => AssignChef(customer, order, tail)
              }
              Behaviors.same
          }
        case ChefAssigned(orderId) =>
          ctx.log.info(s"Waiter assigned a chef to orderId $orderId")
          Behaviors.same
        case ServeOrder(orderId) =>
          ctx.log.info(s"Waiter serves customer with orderId $orderId")
          customers(orderId) ! Customer.Eat
          Behaviors.same
        case _ => Behaviors.same
      }
    }

}
