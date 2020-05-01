package khinkali

import akka.actor.typed.ActorSystem
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {
  val config = ConfigSource.default.load[Config]
  config match {
    case Left(value) =>
      println("Unable to load config: ", value)

    case Right(value) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(value), "Cafe")
      system ! Cafe.Start
  }
}
