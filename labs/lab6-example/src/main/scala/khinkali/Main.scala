package khinkali

import akka.actor.typed.ActorSystem
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {
  val config = ConfigSource.default.load[Config]

  config match {
    case Right(config) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(config), "Cafe")
          system ! Cafe.Start
    case Left(value) =>
      print(s"Error when parsing {}:", value)
  }
}
