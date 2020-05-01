package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.util.Random

object RandomnessManager {
  sealed trait Command
  final case class Generate(from: Double, to: Double, receiver: ActorRef[Double]) extends Command
  def apply(seed: Long): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Generate(from, to, receiver) =>
        val random = new Random(seed)
        receiver ! (from + random.nextDouble() * (to - from))
        Behaviors.same
    }
  }
}