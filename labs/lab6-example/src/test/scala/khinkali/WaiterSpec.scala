package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import akka.testkit.TestActor.Message
import khinkali.Stuffing.{Beef, Mutton}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class WaiterSpec extends AnyFlatSpec  with Matchers {
  import WaiterSpec._

  "Waiter" should "accept orders" in {
    val testKit = BehaviorTestKit(Waiter())
    testKit.run(Waiter.AcceptOrder(customerOrder, customer.ref))
    testKit.selfInbox().receiveMessage() shouldBe Waiter.RequestChief(customerOrder.toOrder(0), customer.ref, List())
  }

  it should "request a free chef to take the order" in {
    val testKit = ActorTestKit("myKit")
    val testChef = testKit.createTestProbe[Chef.Command]()
    val testWaiter = testKit.spawn(Waiter())
    val chefs: List[ActorRef[Chef.Command]] = List(testChef.ref)
    testWaiter ! Waiter.RequestChief(order, customer.ref, chefs)
    testChef.receiveMessage() shouldBe a [Chef.TakeOrder]
  }

  it should "restart request if no chefs are available" in {
    val testKit = ActorTestKit("myKit")
    val testChef = testKit.createTestProbe[Chef.Command]()
    val probe = testKit.createTestProbe[Waiter.Command]()
    val testWaiter = testKit.spawn(Behaviors.monitor(probe.ref, Waiter()))
    val chefs: List[ActorRef[Chef.Command]] = List(testChef.ref)
    testWaiter ! Waiter.RequestChief(order, customer.ref, chefs)
    val message = testChef.receiveMessage()
    val reply = message.asInstanceOf[Chef.TakeOrder].replyTo
    reply ! Result.Busy
    val messages = probe.receiveMessages(3).toList
    messages(0) shouldBe a [Waiter.RequestChief]
    messages(1) shouldBe Waiter.Continue
    messages(2) shouldBe a [Waiter.RequestChief]
  }

  it should "send an Customer.Eat message when receives ServeOrder" in {
    val testCustomer = TestInbox[Customer.Command]()
    val testWaiter = BehaviorTestKit(Waiter())
    testWaiter.run(Waiter.ServeOrder(0, testCustomer.ref))
    testCustomer.receiveMessage() shouldBe Customer.Eat
  }
}

object WaiterSpec {
  val config: ChefConfig = ChefConfig(Boundaries(1, 2))
  val chef: TestInbox[Chef.Command] = TestInbox[Chef.Command]()
  val chefs: List[ActorRef[Chef.Command]] = List(chef.ref)
  val customerOrder: CustomerOrder = CustomerOrder(List(Khinkali(Mutton, 2)))
  val order: Order = Order(2, List(Khinkali(Beef, 2)))
  val customer: TestInbox[Customer.Command] = TestInbox[Customer.Command]()
}
