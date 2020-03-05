package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Waiter {

  sealed trait Command

  case class ReceiveOrder(order: CustomerOrder, replyTo: ActorRef[Customer.Command]) extends Command

  case class DeliverOrder(order: CookedOrder) extends Command

  case class SubmitOrderToChef(order: Order) extends Command

  case object Continue extends Command

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(chefs: ActorRef[Chef.Command]): Behavior[Command] =
    loop(chefs, Map[Int, ActorRef[Customer.Command]](), 0)

  def loop(chefs: ActorRef[Chef.Command], returnMapping: Map[Int, ActorRef[Customer.Command]], counter: Int): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case ReceiveOrder(order, backRef) =>
          val chefsOrder = order.toOrder(counter)
          ctx.log.info(s"Order #${chefsOrder.orderId} from customer ${backRef.path.name} registered.")
          ctx.self ! SubmitOrderToChef(chefsOrder)
          // register order - return updated self state
          loop(chefs, returnMapping.updated(counter, backRef), counter + 1)
        case SubmitOrderToChef(chefsOrder: Order) =>
          implicit val timeout: Timeout = Timeout(1.second)
          ctx.ask(chefs, Chef.TakeOrder(chefsOrder, ctx.self, _: ActorRef[Result])) {
            case Success(Result.Ok) =>
              ctx.log.info(s"Order #${chefsOrder.orderId} accepted.")
              Continue
            case Success(Result.Busy) =>
              ctx.log.info(s"Selected Chef is busy. Try next.")
              // To overcome flooding/ddos
              ctx.scheduleOnce(1.second, ctx.self, SubmitOrderToChef(chefsOrder))
              Continue
            case Failure(exception) =>
              ctx.log.error(exception.getMessage)
              SubmitOrderToChef(chefsOrder)
          }
          Behaviors.same
        case Continue => Behaviors.same
        case DeliverOrder(order) =>
          returnMapping.get(order.orderId) match {
            case Some(ref) =>
              ctx.log.info(s"Order #${order.orderId} sent to customer ${ref.path.name}.")
              ref ! Customer.Eat(order)
            case _ => ctx.log.error(s"Order #${order.orderId} cooked but receiver customer not found.")
          }
          loop(chefs, returnMapping.removed(order.orderId), counter)
      }
  }

}
