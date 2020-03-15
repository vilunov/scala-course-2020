package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.Effect.Spawned
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import akka.actor.typed.ActorSystem
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.flatspec.AnyFlatSpec
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

import scala.util.Random

class CafeSpec extends AnyFlatSpec  with BeforeAndAfterAll with Matchers {
  "Cafe" should "Spawn characters" in {
    val config = ConfigSource.default.load[Config]
    config match {
      case Left(value) =>
        println("Unable to load config: ", value)

      case Right(value) =>
        val behaviourKit = BehaviorTestKit(Cafe(value))

        behaviourKit.run(Cafe.Start)

        // Если что вот эта хрень выдает вот такую ошибку
        // assertion failed: expected: Spawned(Receive(Chef.scala:17-26), Chef1, EmptyProps) but found Spawned(Receive(Chef.scala:17-26), Chef1, EmptyProps)
        // behaviourKit.expectEffect(Effect.Spawned(Chef(new Random(value.randomSeed), value.minCookingTime, value.maxCookingTime), "Chef1"))

        val effects = behaviourKit.retrieveAllEffects()

        effects.collect({
          case Spawned(_, name, _) => name
        }).count(_.contains("Chef")) shouldBe value.nChefs

        effects.collect({
          case Spawned(_, name, _) => name
        }).count(_ == "Waiter") shouldBe 1

        effects.collect({
          case Spawned(_, name, _) => name
        }).count(_.contains("Customer")) shouldBe value.nCustomers
    }
  }
}
