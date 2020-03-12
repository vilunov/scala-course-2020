package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout
import khinkali.Chef.{OrderStatus, TakeOrder}

import scala.concurrent.duration._

object Waiter {
  sealed trait Command

  case class ReceiveOrder(customerOrder: CustomerOrder, replyTo: ActorRef[Customer.Eat.type]) extends Command
  case class ServerOrder(orderId: Int)                                                        extends Command
  case class TryGiveToChef(order: Order)                                                      extends Command
  case class ChefResponse(orderStatus: OrderStatus)                                           extends Command

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(chefs: List[ActorRef[Chef.TakeOrder]]): Behavior[Command] = receiveAndServe(0, chefs, Map())

  def receiveAndServe(
      nextOrderId: Int,
      chefs: List[ActorRef[Chef.TakeOrder]],
      orderCustomerMap: Map[Int, ActorRef[Customer.Eat.type]],
  ): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case ReceiveOrder(customerOrder, replyTo) =>
        ctx.self ! TryGiveToChef(customerOrder.toOrder(nextOrderId))
        receiveAndServe(nextOrderId + 1, chefs, orderCustomerMap + (nextOrderId -> replyTo))

      case ServerOrder(orderId) =>
        orderCustomerMap(orderId) ! Customer.Eat
        Behaviors.same

      case TryGiveToChef(order) =>
        val head :: tail = chefs
        head ! Chef.TakeOrder(order, ctx.self)
        receiveAndServe(nextOrderId, chefs, orderCustomerMap)

      case ChefResponse(orderStatus) =>
        orderStatus match {
          case OrderStatus.Accepted(order) => Behaviors.same

          case OrderStatus.Rejected(order) =>
            val head :: tail = chefs
            ctx.self ! TryGiveToChef(order)
            receiveAndServe(nextOrderId, tail ::: List(head), orderCustomerMap)

          case OrderStatus.Finished(order) =>
            ctx.self ! ServerOrder(order.orderId)
            Behaviors.same
        }
      case _ => Behaviors.same
    }
  }

}
