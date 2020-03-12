package khinkali

import akka.actor.typed.ActorSystem
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.Await
import scala.concurrent.duration._

sealed trait CookingTime
case class BeefTime(from: Double, to: Double) extends CookingTime
case class MuttonTime(from: Double, to: Double) extends CookingTime
case class CheeseAndMushroomsTime(from: Double, to: Double) extends CookingTime

final case class CafeConfig(customersCnt: Int,
                            chefsCnt: Int,
                            waitingTime: (Double, Double),
                            eatingTime: (Double, Double),
                            cookingTimes: List[CookingTime])

object Main extends App {
  ConfigSource.default.load[CafeConfig] match {
    case Right(cfg) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(cfg), "Cafe")
      system ! Cafe.Start
      time { Await.result(system.whenTerminated, 10000.second) }
    case Left(errors) =>
      println(s"[ERROR]: can't load config file - $errors")
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    println("Total time to process all customers: " + (t1 - t0) / 1e9 + "s")
    result
  }
}