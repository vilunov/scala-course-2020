package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.util.Random

object RandomGenerator {
  sealed trait Command
  final case class Generate(from: Double, to: Double, receiver: ActorRef[Double]) extends Command

  def apply(): Behavior[Command] = Behaviors.setup { ctx =>
    val random = new Random()

    Behaviors.receiveMessage {
      case Generate(from, to, receiver) =>
        receiver ! (from + random.nextDouble() * (to - from))
        Behaviors.same
      case _ => Behaviors.same
    }
  }

}