package khinkali

import akka.actor.typed.ActorSystem
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {

  val config = ConfigSource.default
    .load[Config]

  config match {
    case Left(value) =>
      print(s"Error $value")
    case Right(config) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(config), Constants.cafe)
      system ! Cafe.Start
  }
}
