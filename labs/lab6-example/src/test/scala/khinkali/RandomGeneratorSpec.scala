package khinkali

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RandomGeneratorSpec extends AnyFlatSpec with Matchers {

  "RandomGenerator" should "generate random number for each call" in {
    val inbox = TestInbox[Double]()
    val kit = BehaviorTestKit(RandomGenerator())
    val (from, to): (Double, Double) = (0, 5)
    kit.run(RandomGenerator.Generate(from, to, inbox.ref))
    kit.run(RandomGenerator.Generate(from, to, inbox.ref))
    val messages = inbox.receiveAll()

    messages.length shouldEqual 2
    messages.head != messages.last shouldEqual true
  }

  "RandomGenerator" should "generate random number in specified range" in {
    val inbox = TestInbox[Double]()
    val kit = BehaviorTestKit(RandomGenerator())
    val (from1, to1): (Double, Double) = (0, 5)
    val (from2, to2): (Double, Double) = (5, 10)
    kit.run(RandomGenerator.Generate(from1, to1, inbox.ref))
    kit.run(RandomGenerator.Generate(from2, to2, inbox.ref))
    val messages = inbox.receiveAll()

    messages.length shouldEqual 2
    (from1 <= messages.head && messages.head < to1) shouldEqual true
    (from2 <= messages.last && messages.last < to2) shouldEqual true
  }

}