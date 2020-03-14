package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CafeSpec extends AnyFlatSpec with Matchers with OptionValues {
  import CafeSpec._

  "Cafe" should "should create correct amount of actors" in {
    // Arrange
    val behaviorTestKit = BehaviorTestKit(Cafe(config))
    behaviorTestKit.run(Cafe.Start)

    // Act
    val actors = behaviorTestKit.retrieveAllEffects().collect { case Effect.Spawned(_, name, _) => name }
    val chefsCnt = actors.count(_.contains("Chef"))
    val customersCnt = actors.count(_.contains("Customer"))

    // Assert
    (chefsCnt, customersCnt) shouldEqual (config.chefsCount, config.guestsCount)
  }

  "Cafe" should "not be alive after Stop command" in {
    // Arrange
    val behaviorTestKit = BehaviorTestKit(Cafe(config))

    // Act
    behaviorTestKit.run(Cafe.Start)
    behaviorTestKit.run(Cafe.Stop)

    // Assert
    behaviorTestKit.isAlive shouldEqual false
  }
}

object CafeSpec {
  private val config = CafeConfig(5, 5, (2, 2), List(BeefTime(1, 3), ChickenTime(2, 4), VeganTime(1, 5)), 5, 42)
}