package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Stuffing._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._
import pureconfig.ConfigSource
//
class ChefSpec extends AnyFlatSpec with Matchers {
  import ChefSpec._
  "Chef" should "take and finish an order" in {
    val testRes: TestInbox[Result] = TestInbox[Result]()
    val testKit = BehaviorTestKit(Chef(testWaiter.ref, config, seed))
    testKit.run(Chef.TakeOrder(order1, testRes.ref, customer.ref))
    testKit.expectEffect(Effect.Scheduled(2.seconds, testKit.ref, Chef.FinishOrder(1, customer.ref)))
    testRes.receiveMessage() shouldBe Result.Ok
  }
  it should "Send Result.Busy to the waiter if cooking" in {
    val testRes: TestInbox[Result] = TestInbox[Result]()
    val testKit = BehaviorTestKit(Chef.cook(testWaiter.ref, config))
    testKit.run(Chef.TakeOrder(order1, testRes.ref, customer.ref))
    testRes.receiveMessage() shouldBe Result.Busy
  }

  it should "Send Waiter.ServerOrder to the waiter once done cooking" in {
    val testWaiter: TestInbox[Waiter.Command] = TestInbox[Waiter.Command]()
    val testKit = BehaviorTestKit(Chef.cook(testWaiter.ref, config))
    testKit.run(Chef.FinishOrder(1, customer.ref))
    testWaiter.receiveMessage() shouldBe Waiter.ServeOrder(1, customer.ref)
  }

  it should "not react to to FinishOrder when waiting for an order" in {
    val testWaiter: TestInbox[Waiter.Command] = TestInbox[Waiter.Command]()
    val testKit = BehaviorTestKit(Chef(testWaiter.ref, config, seed))
    testKit.run(Chef.FinishOrder(1, customer.ref))
    testWaiter.receiveAll() shouldBe Seq.empty
    testKit.returnedBehavior shouldBe Behaviors.same
  }
}

object ChefSpec {
  val config: ChefConfig = ChefConfig(Boundaries(1, 2))
  val testWaiter: TestInbox[Waiter.Command] = TestInbox[Waiter.Command]()
  val testRes: TestInbox[Result] = TestInbox[Result]()
  val order1: Order = Order(1, List(Khinkali(Mutton, 2)))
  val order2: Order = Order(2, List(Khinkali(Beef, 2)))
  val customer: TestInbox[Customer.Command] = TestInbox[Customer.Command]()
  val seed: Int = 10
}
