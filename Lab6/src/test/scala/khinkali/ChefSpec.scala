package khinkali

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import khinkali.Chef.{FinishOrder, TakeOrder}
import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Random

class ChefSpec extends AnyFlatSpec {

  "Chef" should "accept TakeOrder correct" in {
    val config = Config(1, 5, 2, 0.5, 1.5, 1, 2, 1, 2, 0.5, 1.5, 0.2, 0.5)
    val random = new Random(config.randomSeed)
    val testKit = BehaviorTestKit(Chef(config, random))
    val inboxReply = TestInbox[Result]()
    val inbox = TestInbox[Waiter.Command]()
    val order = Order(1, List(Khinkali(Stuffing.Beef, 5)))
    testKit.run(TakeOrder(order, inboxReply.ref, inbox.ref, Nil))
    inboxReply.expectMessage(Result.Ok)
  }

  "Chef" should "reject TakeOrder correct" in {
    val config = Config(1, 5, 2, 0.5, 1.5, 1, 2, 1, 2, 0.5, 1.5, 0.2, 0.5)
    val random = new Random(config.randomSeed)
    val testKit = BehaviorTestKit(Chef(config, random))
    val inboxReply = TestInbox[Result]()
    val inbox = TestInbox[Waiter.Command]()
    val order = Order(1, List(Khinkali(Stuffing.Beef, 5)))
    testKit.run(TakeOrder(order, inboxReply.ref, inbox.ref, Nil))
    inboxReply.expectMessage(Result.Ok)

    val orderRejected = Order(2, List(Khinkali(Stuffing.Mutton, 3)))
    testKit.run(TakeOrder(orderRejected, inboxReply.ref, inbox.ref, Seq.empty))
    inboxReply.expectMessage(Result.Busy(orderRejected, Seq.empty))
  }

  "Chef" should "accept FinishOrder correct" in {
    val config = Config(1, 5, 2, 0.5, 1.5, 1, 2, 1, 2, 0.5, 1.5, 0.2, 0.5)
    val random = new Random(config.randomSeed)
    val testKit = BehaviorTestKit(Chef(config, random))
    val inboxReply = TestInbox[Result]()
    val inbox = TestInbox[Waiter.Command]()
    val order = Order(1, List(Khinkali(Stuffing.Beef, 5)))
    testKit.run(TakeOrder(order, inboxReply.ref, inbox.ref, Nil))
    inboxReply.expectMessage(Result.Ok)

    testKit.run(FinishOrder(order.orderId, inbox.ref))
    inbox.expectMessage(Waiter.ServeOrder(order.orderId))
  }

  "Chef" should "accept TakeOrder after FinishOrder correct" in {
    val config = Config(1, 5, 2, 0.5, 1.5, 1, 2, 1, 2, 0.5, 1.5, 0.2, 0.5)
    val random = new Random(config.randomSeed)
    val testKit = BehaviorTestKit(Chef(config, random))
    val inboxReply = TestInbox[Result]()
    val inbox = TestInbox[Waiter.Command]()
    val order = Order(1, List(Khinkali(Stuffing.Beef, 5)))
    testKit.run(TakeOrder(order, inboxReply.ref, inbox.ref, Nil))
    inboxReply.expectMessage(Result.Ok)

    val orderRejected = Order(2, List(Khinkali(Stuffing.Mutton, 3)))
    testKit.run(TakeOrder(orderRejected, inboxReply.ref, inbox.ref, Seq.empty))
    inboxReply.expectMessage(Result.Busy(orderRejected, Seq.empty))

    testKit.run(FinishOrder(order.orderId, inbox.ref))
    inbox.expectMessage(Waiter.ServeOrder(order.orderId))

    testKit.run(TakeOrder(orderRejected, inboxReply.ref, inbox.ref, Nil))
    inboxReply.expectMessage(Result.Ok)

    testKit.run(FinishOrder(orderRejected.orderId, inbox.ref))
    inbox.expectMessage(Waiter.ServeOrder(orderRejected.orderId))
  }
}
