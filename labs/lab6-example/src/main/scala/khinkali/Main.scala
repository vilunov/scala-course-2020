package khinkali

import akka.actor.typed.ActorSystem
import pureconfig.ConfigSource
import khinkali.ServiceConf._
import pureconfig.generic.auto._

object Main extends App {
  ConfigSource.default.load[ServiceConf] match {
    case Left(failures) =>
      println("Can't read configs:")
      println(failures)
    case Right(conf: khinkali.ServiceConf) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(conf), "Cafe")
      system ! Cafe.Start
  }
}
