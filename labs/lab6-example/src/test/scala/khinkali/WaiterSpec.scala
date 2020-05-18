package khinkali

import akka.actor.testkit.typed.Effect.Scheduled
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import khinkali.Customer.{LeaveOrder, Start}
import khinkali.Waiter.{ FinishOrder}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WaiterSpec extends AnyFlatSpec with Matchers{
  val r = scala.util.Random
  val conf = CafeConfig(1, 1, 500, 100, 10, 500, 100, 500, 100 )

  "Waiter" should "wait for chefs" in {
    val testKitWaiter = BehaviorTestKit(Waiter())
    val chef1 = TestInbox[Chef.Command]()
    val chef2 = TestInbox[Chef.Command]()
    testKitWaiter.run(Waiter.CameChefs(IndexedSeq(chef1.ref, chef2.ref)))
  }

// taking orders from several customers
  "Waiter" should "take order from several customers" in {
    val inboxCafe = TestInbox[Cafe.Command]()
    val r = scala.util.Random
    val conf = CafeConfig(1, 1, 500, 100, 10, 0, 0, 500, 500)

    val testKitWaiter = TestInbox[Waiter.Command]()
    val testKit = BehaviorTestKit(Customer(testKitWaiter.ref, inboxCafe.ref, conf, r))
    val testKit2 = BehaviorTestKit(Customer(testKitWaiter.ref, inboxCafe.ref, conf, r))
    testKit.run(Start)
    testKit.expectEffectType[Scheduled[LeaveOrder.type]]
    testKit2.run(Start)
    testKit2.expectEffectType[Scheduled[LeaveOrder.type]]

    val order1 = CustomerOrder(List(Khinkali(Stuffing.Beef, 3),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 0)))

    val order2 = CustomerOrder(List(Khinkali(Stuffing.Beef, 5),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 3)))

    testKit.run(LeaveOrder(order1))
    testKitWaiter.expectMessage(Waiter.TakeOrder(order1, testKit.ref))
    testKit2.run(LeaveOrder(order2))
    testKitWaiter.expectMessage(Waiter.TakeOrder(order2, testKit2.ref))

  }

  "Waiter" should "give order to chef" in {
    val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 3),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 0)))
    val testKitWaiter = BehaviorTestKit(Waiter())
    val chef1 = TestInbox[Chef.Command]()
    val chef2 = TestInbox[Chef.Command]()
    val customer = TestInbox[Customer.Command]()

    testKitWaiter.run(Waiter.CameChefs(IndexedSeq(chef1.ref, chef2.ref)))
    testKitWaiter.run(Waiter.TakeOrder(order, customer.ref))
    val rec = chef1.receiveMessage()
    rec.isInstanceOf[Chef.TakeOrder]

  }

  "Waiter" should "schedule orders to chefs" in {
    val testKitWaiter = BehaviorTestKit(Waiter())
    val chef1 = TestInbox[Chef.Command]()
    val customer1 = TestInbox[Customer.Command]()
    val customer2 = TestInbox[Customer.Command]()
    val order1 = CustomerOrder(List(Khinkali(Stuffing.Beef, 3),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 0)))

    val order2 = CustomerOrder(List(Khinkali(Stuffing.Beef, 5),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 3)))
    testKitWaiter.run(Waiter.CameChefs(IndexedSeq(chef1.ref)))

    testKitWaiter.run(Waiter.TakeOrder(order1, customer1.ref))
    testKitWaiter.run(Waiter.TakeOrder(order2, customer2.ref))
    val rec = chef1.receiveMessage()
    rec.isInstanceOf[Chef.TakeOrder]
    testKitWaiter.run(FinishOrder(customer1.ref))
    val rec2 = chef1.receiveMessage()
    rec2.isInstanceOf[Chef.TakeOrder]
  }

  "Waiter" should "give ready orders to the customer" in {
    val testKitWaiter = BehaviorTestKit(Waiter())
    val chef1 = TestInbox[Chef.Command]()
    val chef2 = TestInbox[Chef.Command]()
    testKitWaiter.run(Waiter.CameChefs(IndexedSeq(chef1.ref, chef2.ref)))
    val customer = TestInbox[Customer.Command]()
    testKitWaiter.run(FinishOrder(customer.ref))
    customer.expectMessage(Customer.Eat)
  }

}
