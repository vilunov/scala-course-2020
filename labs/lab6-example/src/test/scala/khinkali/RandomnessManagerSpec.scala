package khinkali

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RandomnessManagerSpec extends AnyFlatSpec with Matchers with OptionValues {
  import RandomnessManagerSpec._

  "RandomnessManager" should "generate one number for each call" in {
    // Arrange
    val inbox = TestInbox[Double]()
    val kit = BehaviorTestKit(RandomnessManager(config.seed))
    kit.run(RandomnessManager.Generate(0, 1, inbox.ref))
    kit.run(RandomnessManager.Generate(0, 1, inbox.ref))

    // Act
    val messages = inbox.receiveAll()

    // Assert
    messages.length shouldEqual 2
  }

  "RandomGenerator" should "generate numbers in specified range" in {
    //Arrange
    val inbox = TestInbox[Double]()
    val behaviorTestKit = BehaviorTestKit(RandomnessManager(config.seed))
    behaviorTestKit.run(RandomnessManager.Generate(0, 1, inbox.ref))

    //Act
    val messages = inbox.receiveAll()

    // Assert
    (0 <= messages.head && messages.head <= 1) shouldEqual true
  }
}

object RandomnessManagerSpec {
  private val config = CafeConfig(5, 5, (2, 2), List(BeefTime(1, 3), ChickenTime(2, 4), VeganTime(1, 5)), 5, 42)
}