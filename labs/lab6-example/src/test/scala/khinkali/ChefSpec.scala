package khinkali

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec

class ChefSpec extends AnyFlatSpec {
  "Chef" should "accept order if free" in {
    val testKit   = BehaviorTestKit(Chef())
    val testInbox = TestInbox[Waiter.ChefResponse]()
    val order     = Order(0, List())
    testKit.run(Chef.TakeOrder(order, testInbox.ref))
    testInbox.expectMessage(Waiter.ChefResponse(Chef.OrderStatus.Accepted(order)))
  }

  "Chef" should "reject order if cooking" in {
    val testInbox = TestInbox[Waiter.ChefResponse]()
    val testKit   = BehaviorTestKit(Chef.produceKhinkali(testInbox.ref))
    val order     = Order(0, List())
    testKit.run(Chef.TakeOrder(order, testInbox.ref))
    testInbox.expectMessage(Waiter.ChefResponse(Chef.OrderStatus.Rejected(order)))
  }
}
