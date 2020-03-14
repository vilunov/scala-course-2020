package khinkali

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Customer.{Eat, Leave, LeaveOrder, Start}
import khinkali.Waiter.TakeOrder
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class CustomerSpec extends AnyFlatSpec with Matchers {

  "Customer" should "process everything correct" in {
    val config = Config(1, 5, 2, 0.5, 1.5, 1, 2, 1, 2, 0.5, 1.5, 0.2, 0.5)
    val random = new Random(config.randomSeed)
    val inbox = TestInbox[Waiter.Command]()
    val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 5)))
    val testKit = BehaviorTestKit(Customer(config, random, inbox.ref, order))
    testKit.run(Start)
    testKit.run(LeaveOrder(order))
    inbox.expectMessage(TakeOrder(order, testKit.ref))
    testKit.run(Eat)
    testKit.run(Leave)
    testKit.currentBehavior shouldEqual Behaviors.stopped
  }
}
