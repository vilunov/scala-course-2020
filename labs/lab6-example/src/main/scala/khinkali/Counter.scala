package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

object Counter {
  sealed abstract class Command
  case object Increment extends Command
  final case class Retrieve(replyTo: ActorRef[Int]) extends Command

  def apply(): Behavior[Command] = withCounterValue(0)

  private def withCounterValue(value: Int): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Increment =>
        val newValue = value + 1
        withCounterValue(newValue)
      case Retrieve(replyTo) =>
        replyTo ! value
        Behaviors.same
    }
  }
}
