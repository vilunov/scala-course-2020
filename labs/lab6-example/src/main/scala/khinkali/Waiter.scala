package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Waiter {

  sealed trait WaiterLogMessage

  implicit def msg2str(msg: WaiterLogMessage): String = msg.toString

  case class OrderRegisteredMessage(orderId: Int, backRefPathName: String) extends WaiterLogMessage {
    override def toString: String =
      s"Order #${orderId} from customer ${backRefPathName} registered."
  }

  case class OrderAcceptedMessage(orderId: Int) extends WaiterLogMessage {
    override def toString: String =
      s"Order #${orderId} accepted."
  }

  object BusyResultMessage extends WaiterLogMessage {
    override def toString: String = s"Selected Chef is busy. Try next."
  }
  case class DeliveredMessage(orderId: Int, refPathName: String) extends WaiterLogMessage {
    override def toString: String =
      s"Order #${orderId} sent to customer ${refPathName}."
  }
  case class MissedCustomerMessage(orderId: Int) extends WaiterLogMessage {
    override def toString: String =
      s"Order #${orderId} cooked but receiver customer not found."
  }

  sealed trait Command

  case class ReceiveOrder(order: CustomerOrder, replyTo: ActorRef[Customer.Command]) extends Command

  case class DeliverOrder(order: CookedOrder) extends Command

  case class SubmitOrderToChef(order: Order) extends Command

  case object Continue extends Command


  val counterStart: Int = 0

  def apply(chefs: ActorRef[Chef.Command], conf: WaiterConf): Behavior[Command] =
    loop(chefs, Map[Int, ActorRef[Customer.Command]](), counterStart, conf)

  def loop(chefs: ActorRef[Chef.Command], returnMapping: Map[Int, ActorRef[Customer.Command]], counter: Int, conf: WaiterConf): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>

      implicit val timeout: Timeout = Timeout(conf.chefRespondTimeout.second)
      msg match {
        case ReceiveOrder(order, backRef) =>
          val chefsOrder = order.toOrder(counter)
          ctx.log.info(OrderRegisteredMessage(chefsOrder.orderId, backRef.path.name))
          ctx.self ! SubmitOrderToChef(chefsOrder)
          // register order - return updated self state
          loop(chefs, returnMapping.updated(counter, backRef), counter + 1, conf)
        case SubmitOrderToChef(chefsOrder: Order) =>
          ctx.ask(chefs, Chef.TakeOrder(chefsOrder, ctx.self, _: ActorRef[Result])) {
            case Success(Result.Ok) =>
              ctx.log.info(OrderAcceptedMessage(chefsOrder.orderId))
              Continue
            case Success(Result.Busy) =>
              ctx.log.info(BusyResultMessage)
              // To overcome flooding/ddos
              ctx.scheduleOnce(conf.resendTimeout.second, ctx.self, SubmitOrderToChef(chefsOrder))
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
              ctx.log.info(DeliveredMessage(order.orderId, ref.path.name))
              ref ! Customer.Eat(order)
            case _ => ctx.log.error(MissedCustomerMessage(order.orderId))
          }
          loop(chefs, returnMapping.removed(order.orderId), counter, conf)
      }
  }

}
