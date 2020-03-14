package khinkali

import akka.actor.ActorSystem
import akka.actor.testkit.typed.Effect.{Scheduled}
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import akka.testkit.{ImplicitSender, TestKit}
import khinkali.Customer.{LeaveOrder, Start}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike


class CustomerSpec extends TestKit(ActorSystem("CustomerSpec")) with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Customer" should {
    "go to leaveOrder stage after start" in {
      val r = scala.util.Random
      val conf = CafeConfig(1, 1, 500, 100, 10, 500, 500, 500, 500)
      val inboxWaiter = TestInbox[Waiter.Command]()
      val inboxCafe = TestInbox[Cafe.Command]()
      val testKit = BehaviorTestKit(Customer(inboxWaiter.ref, inboxCafe.ref, conf, r))
      val dur = 500.millisecond
      testKit.run(Start)
      testKit.expectEffectType[Scheduled[LeaveOrder.type]]
    }

// this also tests that waiter takes order from one customer
    "leave Order" in {
      val inboxCafe = TestInbox[Cafe.Command]()
      val r = scala.util.Random
      val conf = CafeConfig(1, 1, 500, 100, 10, 0, 0, 500, 500)

      val testKitWaiter = TestInbox[Waiter.Command]()
      val testKit = BehaviorTestKit(Customer(testKitWaiter.ref, inboxCafe.ref, conf, r))
      testKit.run(Start)
      testKit.expectEffectType[Scheduled[LeaveOrder.type]]

      // todo: NOT TESTABLE, https://github.com/akka/akka/issues/27514
      val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 3),
        Khinkali(Stuffing.Mutton, 0),
        Khinkali(Stuffing.CheeseAndMushrooms, 0)))
      testKit.run(LeaveOrder(order))
      testKitWaiter.expectMessage(Waiter.TakeOrder(order, testKit.ref))
    }


    "eat after receiving a dish and leave" in {
      val childActor = Behaviors.receiveMessage[Customer.Command] { _ =>
        Behaviors.same[Customer.Command]
      }

      val inboxCafe = TestInbox[Cafe.Command]()
      val r = scala.util.Random
      val conf = CafeConfig(1, 1, 500, 100, 10, 0, 0, 50, 50)

      val testKitWaiter = TestInbox[Waiter.Command]()
      val testKit = BehaviorTestKit(Customer(testKitWaiter.ref, inboxCafe.ref, conf, r))
      testKit.run(Start)
      testKit.expectEffectType[Scheduled[LeaveOrder.type]]

      // todo: NOT TESTABLE, https://github.com/akka/akka/issues/27514
      val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 3),
        Khinkali(Stuffing.Mutton, 0),
        Khinkali(Stuffing.CheeseAndMushrooms, 0)))
      testKit.run(LeaveOrder(order))
      testKit.run(Customer.Eat)
      testKit.expectEffect(Scheduled(50.millisecond, testKit.ref, Customer.Leave))

    }

  }
}
