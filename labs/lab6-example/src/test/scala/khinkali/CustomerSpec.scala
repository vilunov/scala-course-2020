package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Stuffing.{Beef, Mutton}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._
import scala.util.Random

class CustomerSpec extends AnyFlatSpec with Matchers {
  import CustomerSpec._

  "Customer" should "choose and make an order" in {
    val testKit = BehaviorTestKit(Customer(waiter.ref, config, seed))
    testKit.run(Customer.Start)
    testKit.expectEffect(Effect.Scheduled(1.second, testKit.ref, Customer.LeaveOrder(customerOrder)))
    testKit.currentBehavior should not be Behaviors.same
  }

  it should "leave the order" in {
    val testKit = BehaviorTestKit(Customer.leaveOrder(config, waiter.ref, new Random(seed)))
    testKit.run(Customer.LeaveOrder(customerOrder))
    waiter.receiveMessage() shouldBe Waiter.AcceptOrder(customerOrder, testKit.ref)
    testKit.currentBehavior should not be Behaviors.same
  }

  it should "eat with scheduled leaving" in {
    val testKit = BehaviorTestKit(Customer.waitForEat(config, new Random(seed)))
    testKit.run(Customer.Eat)
    testKit.expectEffect(Effect.Scheduled(1.second, testKit.ref, Customer.Leave))
    testKit.currentBehavior should not be Behaviors.same
  }

  it should "leave" in {
    val testKit = BehaviorTestKit(Customer.waitToLeave)
    testKit.run(Customer.Leave)
    testKit.currentBehavior shouldBe Behaviors.stopped
  }

}

object CustomerSpec {
  val config: CustomerConfig = CustomerConfig(Boundaries(1, 2), Boundaries(1, 2), Boundaries(1, 2), Boundaries(1, 2))
  val waiter: TestInbox[Waiter.Command] = TestInbox[Waiter.Command]()
  val customerOrder: CustomerOrder = CustomerOrder(List(Khinkali(Beef, 1)))
  val order: Order = Order(2, List(Khinkali(Beef, 2)))
  val seed: Int = 10
}
