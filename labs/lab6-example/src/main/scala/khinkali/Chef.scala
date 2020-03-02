package khinkali

import akka.actor.typed.{ActorRef, Behavior}

object Chef {
  sealed trait Command

  case class TakeOrder(order: Order, replyTo: ActorRef[Result], customer: ActorRef[Customer.Eat.type]) extends Command
  case class FinishOrder(orderId: Int, customer: ActorRef[Customer.Eat.type]) extends Command

  def apply(): Behavior[Command] = ???
}
