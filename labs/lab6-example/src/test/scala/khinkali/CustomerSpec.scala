package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.{BeforeAndAfterAll, Matchers}

import scala.concurrent.duration._
import scala.util.Random

class CustomerSpec extends AnyFlatSpec  with BeforeAndAfterAll with Matchers {
  "Customer" should "Make orders" in {
    val testWaiter = TestInbox[Waiter.Command]()
    val customerOrder = CustomerOrder(List(Khinkali(Stuffing.Beef, 10)))

    val behaviourKit = BehaviorTestKit(Customer(testWaiter.ref, customerOrder, new Random(), 5, 5, 5, 5))

    behaviourKit.run(Customer.Start)
    behaviourKit.expectEffect(Effect.Scheduled(5.second, behaviourKit.ref, Customer.LeaveOrder(customerOrder)))

    behaviourKit.run(Customer.LeaveOrder(customerOrder))
    testWaiter.expectMessage(Waiter.ReceiveOrder(customerOrder, behaviourKit.ref))
  }

  "Customer" should "Eat then leave" in {
    val testWaiter = TestInbox[Waiter.Command]()
    val customerOrder = CustomerOrder(List(Khinkali(Stuffing.Beef, 10)))

    val behaviourKit = BehaviorTestKit(Customer(testWaiter.ref, customerOrder, new Random(), 5, 5, 5, 5))

    behaviourKit.run(Customer.Start)
    behaviourKit.run(Customer.LeaveOrder(customerOrder))
    behaviourKit.run(Customer.Eat)

    behaviourKit.expectEffect(Effect.Scheduled(5.second, behaviourKit.ref, Customer.LeaveOrder(customerOrder)))
    behaviourKit.expectEffect(Effect.Scheduled(5.second, behaviourKit.ref, Customer.Leave))

    //behaviourKit.run(Customer.Leave)
    //behaviourKit.expectEffect(Effect.Stopped("child"))
  }
}
