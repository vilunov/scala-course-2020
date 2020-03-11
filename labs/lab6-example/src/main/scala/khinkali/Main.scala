package khinkali

import akka.actor.typed.ActorSystem

object Main extends App {
  val system: ActorSystem[Cafe.Command] =
    ActorSystem(Cafe(Config.customerConfig.N, Config.chefConfig.N), "Cafe")
  system ! Cafe.Start
}
