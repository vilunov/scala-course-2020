package khinkali

import org.scalatest.flatspec.AnyFlatSpec
import khinkali.Chef._
import khinkali.Stuffing.CheeseAndMushrooms
import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.Effect.Scheduled
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import akka.actor.testkit.typed.scaladsl.TestInbox
import org.slf4j.event.Level


class ChefSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {

  import ChefSpec._

  "Chef when free" should "take order and log it's id" in {

    val testChef = BehaviorTestKit(Chef(chefConf))
    val resultReceiver = TestInbox[Result]("ResultReceiver")
    val orderReceiver = TestInbox[Waiter.Command]("OrderReceiver")
    testChef.run(Chef.TakeOrder(order, orderReceiver.ref, resultReceiver.ref))

    val schedulers = testChef.retrieveAllEffects().collect {
      case s: Scheduled[Chef.Command] => s
    }
    schedulers.size shouldBe 1

    val scheduled = schedulers(0)
    cookingRange.contain(scheduled.delay.toSeconds) shouldBe true
    scheduled.message shouldBe FinishOrder(order)
    testChef.logEntries() shouldBe Seq(CapturedLogEvent(Level.INFO, Chef.AcceptedMessage(0)))
  }

  "Chef when free" should "not respond on FinishOrder" in {
    val testChef = BehaviorTestKit(Chef(chefConf))
    testChef.run(Chef.FinishOrder(order))

    testChef.logEntries().isEmpty shouldBe true
    testChef.retrieveAllEffects().isEmpty shouldBe true
  }


  "Chef when busy" should "respond on FinishOrder" in {
    val orderReceiver = TestInbox[Waiter.Command]("OrderReceiver")
    val testChef = BehaviorTestKit(Chef.busyState(orderReceiver.ref, chefConf))
    testChef.run(Chef.FinishOrder(order))

    testChef.logEntries() shouldBe Seq(CapturedLogEvent(Level.INFO, Chef.FinishedMessage(order.orderId)))
    orderReceiver.expectMessage(Waiter.DeliverOrder(cookedOrder))
  }

  "Chef when busy" should "not accept new order" in {
    val resultReceiver = TestInbox[Result]("ResultReceiver")
    val orderReceiver = TestInbox[Waiter.Command]("OrderReceiver")
    val testChef = BehaviorTestKit(Chef.busyState(orderReceiver.ref, chefConf))
    testChef.run(Chef.TakeOrder(order, orderReceiver.ref, resultReceiver.ref))

    resultReceiver.expectMessage(Result.Busy)
    testChef.logEntries() shouldBe Seq(CapturedLogEvent(Level.INFO, Chef.BusyMessage(order.orderId)))
    testChef.retrieveAllEffects() shouldBe Seq()
  }


}

object ChefSpec {
  val cookingRange = DoubleRange(0, 2)
  val chefConf = ChefConf(cookingRange)
  val order = Order(0, List(Khinkali(CheeseAndMushrooms, 1)))
  val cookedOrder: CookedOrder = order.toCookedOrder
}