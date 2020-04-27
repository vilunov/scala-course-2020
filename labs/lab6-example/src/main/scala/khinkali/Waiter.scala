package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

//class Waiter{}

object Waiter {
  sealed trait Command
  implicit val timeout: Timeout = Timeout(1.second)
  case class FinishOrder(customer: ActorRef[Customer.Eat.type]) extends Command
  case class TakeOrder(ordered: CustomerOrder, customer: ActorRef[Customer.Eat.type ]) extends Command
  case class TakeOrderAgain (ordered: CustomerOrder, customer: ActorRef[Customer.Eat.type ]) extends Command
  case class AcceptedOrder (order: Order,customer: ActorRef[Customer.Eat.type], chef:ActorRef[Chef.Command] ) extends Command
  case class Scheduled(order: CustomerOrder, customer: ActorRef[Customer.Eat.type ]) extends Command
  case class CameChefs (chefs: IndexedSeq[ ActorRef[Chef.Command] ]) extends Command


  var idCounter =0

  def apply (): Behavior[Command] ={
    waitForChefs()
  }

  def waitForChefs():Behavior[Command] =
    Behaviors.receive { (cat, mew) =>
      mew match {
        case CameChefs(chefs) => work(0, 0, chefs)
        case _ => Behaviors.same
      }
    }

  def work(orderId: Int, chef:Int, chefs: IndexedSeq[ ActorRef[Chef.Command] ]): Behavior[Command] =

    Behaviors.setup { cat =>
      val ref = cat.messageAdapter[Result]{
          case Result.Ok(ordered, cust , chefAc)   => AcceptedOrder(ordered.toOrder(orderId), cust, chefAc)
          case Result.Busy(ordered, cust) =>  Scheduled(ordered, cust)
      }
      Behaviors.receive { (cat, mew) =>
        mew match {
          case TakeOrder(ordered, cust) =>
            cat.log.info(s"waiter has taken an order from $cust")
            chefs(chef) ! Chef.TakeOrder(ordered,ref, cust)
            var newChef =0
            if (chef+1< chefs.length){
              newChef = chef+1
            }
            else{
              newChef=0
            }
            work(orderId+1, newChef, chefs)
          case TakeOrderAgain(ordered, cust) =>
            cat.log.info(s"waiter has taken an order from $cust")
            chefs(chef) ! Chef.TakeOrder(ordered,ref, cust)
            var newChef =0
            if (chef+1< chefs.length){
              newChef = chef+1
            }
            else{
              newChef=0
            }
            work(orderId, newChef, chefs)
          case Scheduled(order, cust) =>
            cat.log.info(s"waiter is scheduling an order $order for $cust ")
            val custOrder = CustomerOrder(order.dishes)
            cat.scheduleOnce(500.millisecond, cat.self, TakeOrderAgain(custOrder, cust))
            Behaviors.same

          case FinishOrder(customer)=>
            cat.log.info(s"waiter is giving a finished order to a customer: $customer")
            customer ! Customer.Eat
            Behaviors.same
          case _ => Behaviors.same

        }
      }
    }

}
