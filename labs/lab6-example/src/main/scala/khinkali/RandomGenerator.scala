package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.util.Random

object RandomGenerator {
  sealed trait Command
  final case class Generate(from: Double, to: Double, receiver: ActorRef[Double]) extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    val random = new Random()
    msg match {
      case Generate(from, to, receiver) =>
        receiver ! (from + random.nextDouble() * (to - from))
        Behaviors.same
    }
  }
}