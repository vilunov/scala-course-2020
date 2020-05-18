package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import khinkali.Chef.TakeOrder
import khinkali.Waiter.CameChefs
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.duration._
import org.scalatest.matchers.should.Matchers


class ChefSpec extends AnyFlatSpec with Matchers {
  "Chef" should "take the first order" in {
    val r = scala.util.Random
    val conf = CafeConfig(1, 1, 500, 100, 10, 500, 100, 500, 100 )
    val inboxResult = TestInbox[Result]()
    val inboxWaiter = TestInbox[Waiter.Command]()
    val inboxCustomer = TestInbox[Customer.Command]()
    val testKirWaiter = BehaviorTestKit(Waiter())
    val testKit = BehaviorTestKit(Chef(inboxWaiter.ref, conf, r))
    testKirWaiter.run(CameChefs(IndexedSeq(testKit.ref)))

    val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 3),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 0)))
    testKit.run(TakeOrder(order, inboxResult.ref, inboxCustomer.ref))
    val res = inboxResult.receiveMessage()
    res.isInstanceOf[Result.Ok] shouldBe true
  }

  "Chef" should "take the first order and return correct OK" in {
    val r = scala.util.Random
    val conf = CafeConfig(1, 1, 500, 100, 10, 500, 100, 500, 100 )
    val inboxResult = TestInbox[Result]()
    val inboxWaiter = TestInbox[Waiter.Command]()
    val inboxCustomer = TestInbox[Customer.Command]()
    val testKirWaiter = BehaviorTestKit(Waiter())
    val testKit = BehaviorTestKit(Chef(inboxWaiter.ref, conf, r))
    testKirWaiter.run(CameChefs(IndexedSeq(testKit.ref)))

    val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 5),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 0)))
    testKit.run(TakeOrder(order, inboxResult.ref, inboxCustomer.ref))
    inboxResult.expectMessage(Result.Ok(order, inboxCustomer.ref, testKit.ref))
  }

  "Chef" should "return busy for 2nd order before timeout" in {
    val r = scala.util.Random
    val conf = CafeConfig(1, 1, 500, 100, 10, 500, 100, 500, 100 )
    val inboxResult = TestInbox[Result]()
    val inboxWaiter = TestInbox[Waiter.Command]()
    val inboxCustomer = TestInbox[Customer.Command]()
    val testKirWaiter = BehaviorTestKit(Waiter())
    val testKit = BehaviorTestKit(Chef(inboxWaiter.ref, conf, r))
    testKirWaiter.run(CameChefs(IndexedSeq(testKit.ref)))

    val order1 = CustomerOrder(List(Khinkali(Stuffing.Beef, 10),
      Khinkali(Stuffing.Mutton, 10),
      Khinkali(Stuffing.CheeseAndMushrooms, 10)))

    val order2 = CustomerOrder(List(Khinkali(Stuffing.Beef, 1),
      Khinkali(Stuffing.Mutton, 1),
      Khinkali(Stuffing.CheeseAndMushrooms, 1)))
    testKit.run(TakeOrder(order1, inboxResult.ref, inboxCustomer.ref))
    inboxResult.expectMessage(Result.Ok(order1, inboxCustomer.ref, testKit.ref))

    testKit.run(TakeOrder(order2, inboxResult.ref, inboxCustomer.ref))
    inboxResult.expectMessage(Result.Busy(order2, inboxCustomer.ref))
  }

//  this also tests that waiter receives finished order
  "Chef" should "finish cooking" in {
    val r = scala.util.Random
    val conf = CafeConfig(1, 1, 500, 500, 10, 500, 100, 500, 100 )
    val inboxResult = TestInbox[Result]()
    val inboxWaiter = TestInbox[Waiter.Command]()
    val inboxCustomer = TestInbox[Customer.Command]()
    val testKirWaiter = BehaviorTestKit(Waiter())
    val testKit = BehaviorTestKit(Chef(inboxWaiter.ref, conf, r))
    val chef = Chef(inboxWaiter.ref, conf, r)
    val order1 = CustomerOrder(List(Khinkali(Stuffing.Beef, 1),
      Khinkali(Stuffing.Mutton, 0),
      Khinkali(Stuffing.CheeseAndMushrooms, 0)))

    testKit.run(TakeOrder(order1, inboxResult.ref, inboxCustomer.ref))

    inboxResult.expectMessage(Result.Ok(order1, inboxCustomer.ref, testKit.ref))
    testKit.expectEffect(Effect.Scheduled(500.millisecond, testKit.ref, Chef.FinishOrder(inboxCustomer.ref)))
  }

}
