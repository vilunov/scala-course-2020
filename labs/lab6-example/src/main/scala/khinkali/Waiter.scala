package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class Waiter(val chefs: IndexedSeq[ActorRef[Chef.Command]]) {
  import Waiter._

  def start: Behavior[Waiter.Command] = beReady(0)

  def beReady(currentId: Int): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case ReceiveOrder(customerOrder, customer) =>
          val order = customerOrder.toOrder(currentId)
          ctx.self ! Ask(order, 0, customer)
          beReady(currentId + 1)

        case Ask(order, chefId, customer) =>
          val chef = chefs(chefId)
          ctx.ask[Chef.TakeOrder, Result](chef, ref => Chef.TakeOrder(order, ref, customer)) {
            case Success(Result.Ok) =>
              ctx.log.info(s"order ${order.orderId} is assigned to chef ${chefId + 1}")
              Continue

            case Success(Result.Busy) =>
              //ctx.log.info(s"chef $chefId is busy, trying next one")
              if ((chefId + 1) == chefs.length)
                Ask(order, 0, customer)
              else
                Ask(order, chefId + 1, customer)

            case Failure(exception) =>
              ctx.log.error("Failed to send Order", exception)
              Error
          }

          Behaviors.same

        case _ => Behaviors.same
      }
    }
}


object Waiter {
  sealed trait Command

  case object Continue extends Command
  case object Error extends Command
  case class ReceiveOrder(order: CustomerOrder, customer: ActorRef[Customer.Eat.type]) extends Command
  case class Ask(order: Order, chefId: Int, customer: ActorRef[Customer.Eat.type]) extends Command
  case class ServeCustomer(customer: ActorRef[Customer.Eat.type])

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(chefs: IndexedSeq[ActorRef[Chef.Command]]): Behavior[Command] = {
    //beReady(0, chefs)
    new Waiter(chefs).start
  }
}
