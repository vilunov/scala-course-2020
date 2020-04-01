package khinkali

import akka.actor.typed.ActorSystem
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {
  ConfigSource.default.load[Config] match {
    case Left(value) =>
      println("Config is corrupted or not found. Terminating...")
    case Right(config) =>
      val system: ActorSystem[Cafe.Command] =
        ActorSystem(Cafe(config), "Cafe")
      system ! Cafe.Start
  }
}
