package khinkali

import akka.actor.typed.ActorSystem
import khinkali.Stuffing.{Beef, Chicken, Vegan}
import pureconfig._
import pureconfig.generic.auto._
import pureconfig.configurable._
import pureconfig.error.CannotConvert

object Main extends App {
  implicit val stuffingMapReader: ConfigReader[Map[Stuffing, (Double, Double)]] = genericMapReader {
    case Beef.toString    => Right(Stuffing.Beef)
    case Chicken.toString => Right(Stuffing.Chicken)
    case Vegan.toString   => Right(Stuffing.Vegan)
    case default          => Left(CannotConvert(default, "Stuffing", because = "Incorrect stuffing"))
  }

  ConfigSource.default.load[CafeConfig] match {
    case Right(cfg) =>
      val system: ActorSystem[Cafe.Command] = ActorSystem(Cafe(cfg), "Cafe")
      time { system ! Cafe.Start }
    case Left(errors) =>
      println(s"Error $errors")
  }

  def time[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    println("Elapsed time: " + (t1 - t0) + "ns")
    result
  }
}