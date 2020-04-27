package khinkali

import akka.actor.typed.ActorSystem
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {
  ConfigSource.default.load[CafeConfig] match {

    case Right(conf) =>

      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(conf.numOfChefs,conf.numOfCustomers, conf), "Cafe")
      system ! Cafe.Start
    case Left(error) =>
      println("no config")
  }

}
