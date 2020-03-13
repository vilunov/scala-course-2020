package khinkali

import akka.actor.testkit.typed.Effect.Spawned
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.flatspec.AnyFlatSpec
import pureconfig._
import pureconfig.generic.auto._

class CafeSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  "Cafe" should "create actors" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val cafe = BehaviorTestKit(Cafe(config))
        cafe.run(Cafe.Start)
        val spawnedEffects = cafe.retrieveAllEffects().collect { case Spawned(_, name, _) => name }
        spawnedEffects.count(_.contains("Waiter")) shouldBe 1
        spawnedEffects.count(_.contains("Chef")) shouldBe config.numChefs
        spawnedEffects.count(_.contains("Customer")) shouldBe config.numCustomers
      case Left(e) => println(e)
    }
  }
}
