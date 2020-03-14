package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.concurrent.duration._

object Waiter {

  sealed trait Command

  case class TakeOrder(order: CustomerOrder, customer: ActorRef[Customer.Command]) extends Command

  case class ServeOrder(orderId: Int) extends Command

  case class ChefResult(result: Result) extends Command

  def resultAdapter(result: Result): Command = result match {
    case Result.Ok => ChefResult(Result.Ok)
    case Result.Busy(order, tailChefs) => ChefResult(Result.Busy(order, tailChefs))
  }

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(chefs: Seq[ActorRef[Chef.Command]]): Behavior[Command] =
    waitForOrderProcessing(0, Map(), chefs)

  def waitForOrderProcessing(newOrderId: Int,
                             orderToCustomer: Map[Int, ActorRef[Customer.Command]],
                             chefs: Seq[ActorRef[Chef.Command]]): Behavior[Command] = {
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case TakeOrder(orders, customer) =>
          ctx.log.info(s"Taking order $orders")
          val order = Order(newOrderId, orders.dishes)
          val chef = chefs.head
          chef ! Chef.TakeOrder(order, ctx.messageAdapter(resultAdapter), ctx.self, chefs.tail)
          waitForOrderProcessing(newOrderId + 1, orderToCustomer + (newOrderId -> customer), chefs)
        case ServeOrder(orderId) =>
          if (orderToCustomer.contains(orderId)) {
            ctx.log.info(s"Serving order $orderId")
            orderToCustomer(orderId) ! Customer.Eat
            waitForOrderProcessing(newOrderId, orderToCustomer - orderId, chefs)
          } else {
            Behaviors.same
          }
        case ChefResult(Result.Busy(order, tailChefs)) =>
          if (tailChefs.isEmpty) {
            ctx.scheduleOnce(timeout.duration, ctx.self, ChefResult(Result.Busy(order, chefs)))
            Behaviors.same
          } else {
            val chef = tailChefs.head
            chef ! Chef.TakeOrder(order, ctx.messageAdapter(resultAdapter), ctx.self, tailChefs.tail)
            Behaviors.same
          }
        case ChefResult(Result.Ok) => Behaviors.same
        case _ =>
          Behaviors.same
      }
    }
  }
}
