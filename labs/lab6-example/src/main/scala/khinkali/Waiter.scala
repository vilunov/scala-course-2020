package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

object Waiter {

  sealed trait Command

  case object Start extends Command

  case object Processed extends Command

  case class Order(order: CustomerOrder, client: ActorRef[Customer.Command])
      extends Command

  case class Retry(order: khinkali.Order,
                   client: ActorRef[Customer.Command],
                   chefs: Seq[ActorRef[Chef.Command]])
      extends Command

  case class Serve(client: ActorRef[Customer.Eat.type]) extends Command

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(chefs: Seq[ActorRef[Chef.Command]],
            rng: Random): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start => processOrders(chefs, 1, rng)
        case _ =>
          throw new Exception(
            "Message other than START arrived at uninitialized Waiter"
          )
          ctx.log.error(
            "Message other than START arrived at uninitialized Waiter"
          )
          Behaviors.same
      }
    }

  def processOrders(chefs: Seq[ActorRef[Chef.Command]],
                    ordernum: Int,
                    rng: Random): Behavior[Command] = Behaviors.receive {
    (ctx, msg) =>
      msg match {
        case Order(order, client) =>
          val ord = order.toOrder(ordernum)
          ctx.log.info(s"Got order #$ordernum")
          ctx.ask[Chef.Command, Result](
            chefs.head,
            ref => Chef.TakeOrder(ord, ref, ctx.self, client)
          ) {
            case Failure(exception) =>
              println(exception)
              Retry(ord, client, chefs.tail)
            case Success(Result.Busy) =>
              val id = ord.orderId
              Retry(ord, client, chefs.tail)
            case Success(Result.Ok) =>
              val id = ord.orderId
              Processed
          }
          processOrders(chefs, ordernum + 1, rng)
        case Retry(order, client, chefsLeft) =>
          if (chefsLeft.isEmpty) {
            ctx.scheduleOnce(
              rng.between(3, 5).seconds,
              ctx.self,
              Retry(order, client, chefs)
            )
            Behaviors.same
          } else {
            ctx.ask[Chef.Command, Result](
              chefsLeft.head,
              ref => Chef.TakeOrder(order, ref, ctx.self, client)
            ) {
              case Failure(exception) =>
                println(exception)
                Retry(order, client, chefsLeft.tail)
              case Success(Result.Busy) =>
                Retry(order, client, chefsLeft.tail)
              case Success(Result.Ok) =>
                val id = order.orderId
                Processed
            }
            Behaviors.same
          }
        case Processed =>
          Behaviors.same
        case Serve(client) =>
          ctx.log.info(s"Serving order to $client")
          client ! Customer.Eat
          Behaviors.same
        case Start =>
          ctx.log.error(
            "Got a START message while already started. Very strange..."
          )
          Behaviors.same
      }
  }
}
