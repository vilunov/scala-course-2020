package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.Success
import scala.concurrent.duration._

object Waiter {
  sealed trait Command
  implicit val timeout: Timeout = Timeout(1.second)

  object WaitForOrder extends Command
  case class AcceptOrder(order: CustomerOrder, customer: ActorRef[Customer.Command]) extends Command
  case class ProcessOrder(order: Order, customer: ActorRef[Customer.Command]) extends Command
  case class SearchForChef(chefs: Vector[ActorRef[Chef.Command]],
                           order: Order, customer: ActorRef[Customer.Command]) extends Command

  def apply(chefs: Vector[ActorRef[Chef.Command]]): Behavior[Command] = acceptOrder(chefs, 0)

  def acceptOrder(chefs: Vector[ActorRef[Chef.Command]], orderId: Int): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case AcceptOrder(order, customer) =>
          val acceptedOrder = order.toOrder(orderId)
          ctx.log.info(s"Waiter accepted order ${acceptedOrder.orderId} from ${customer.path.name}")
          ctx.self ! ProcessOrder(acceptedOrder, customer)
          acceptOrder(chefs, orderId + 1)

        case ProcessOrder(order, customer) =>
          ctx.self ! SearchForChef(chefs, order, customer)
          Behaviors.same

        case SearchForChef(notAsked, order, customer) =>
          notAsked.headOption match {
            case Some(chef) =>
              ctx.ask(chef, Chef.TakeOrder(order, customer, _: ActorRef[Result])) {
                case Success(Result.Ok) => WaitForOrder
                case _ => SearchForChef(notAsked.tail, order, customer)
              }
              Behaviors.same
            case None =>
              ctx.self ! ProcessOrder(order, customer)
              Behaviors.same
          }

        case _ => Behaviors.same
      }
    }

}
