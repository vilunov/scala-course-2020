package khinkali

import akka.actor.typed.ActorSystem

object Main extends App {
  val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(), "Cafe")
  system ! Cafe.Start
}
