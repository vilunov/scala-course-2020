package khinkali

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Waiter {
  sealed trait Command
  implicit val timeout: Timeout = Timeout(1.second)

  def apply(): Behavior[Command] = ???

}
