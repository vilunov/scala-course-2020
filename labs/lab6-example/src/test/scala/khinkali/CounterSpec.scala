package khinkali

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CounterSpec extends AnyFlatSpec with Matchers {

  "Counter" should "be initialized with 0" in {
    val inbox = TestInbox[Int]()
    val kit = BehaviorTestKit(Counter())
    kit.run(Counter.Retrieve(inbox.ref))
    val messages = inbox.receiveAll()
    messages shouldEqual Seq(0)
  }

  "Counter" should "be incremented correctly" in {
    val inbox = TestInbox[Int]()
    val kit = BehaviorTestKit(Counter())
    for { _ <- 0 until 10 } kit.run(Counter.Increment)
    kit.run(Counter.Retrieve(inbox.ref))
    val messages = inbox.receiveAll()
    messages shouldEqual 10
  }

}
