package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import khinkali.Customer.Eat
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration._
import scala.util.Random

class ChefSpec extends AnyFlatSpec {
  val rngesus = new Random(1)

  "Chef" should "Take orders" in {
    val behaviourKit = BehaviorTestKit(Chef(rngesus, CookingTime(1, 0)))

    val testWaiter = TestInbox[Result]()

    val testCustomer = TestInbox[Customer.Eat.type]()

    val order = Order(0, List(Khinkali(Stuffing.Beef, 10)))

    behaviourKit.run(Chef.TakeOrder(order, testWaiter.ref, testCustomer.ref))
    testWaiter.expectMessage(Result.Ok)
    behaviourKit.expectEffect(Effect.Scheduled(1.second, behaviourKit.ref, Chef.FinishOrder(order.orderId, testCustomer.ref)))
  }

  "Chef" should "Send orders" in {
    val behaviourKit = BehaviorTestKit(Chef(rngesus, CookingTime(1, 0)))

    val testWaiter = TestInbox[Result]()

    val testCustomer = TestInbox[Customer.Eat.type]()

    val order = Order(0, List(Khinkali(Stuffing.Beef, 10)))

    behaviourKit.run(Chef.TakeOrder(order, testWaiter.ref, testCustomer.ref))
    behaviourKit.run(Chef.FinishOrder(order.orderId, testCustomer.ref))

    testCustomer.expectMessage(Eat)
  }

  "Chef" should "Decline orders" in {
    val behaviourKit = BehaviorTestKit(Chef(rngesus, CookingTime(1, 0)))

    val testWaiter = TestInbox[Result]()

    val testCustomer = TestInbox[Customer.Eat.type]()

    val order1 = Order(0, List(Khinkali(Stuffing.Beef, 10)))
    val order2 = Order(0, List(Khinkali(Stuffing.Beef, 10)))

    behaviourKit.run(Chef.TakeOrder(order1, testWaiter.ref, testCustomer.ref))
    behaviourKit.run(Chef.TakeOrder(order2, testWaiter.ref, testCustomer.ref))

    testWaiter.expectMessage(Result.Ok)
    testWaiter.expectMessage(Result.Busy)
  }
}
