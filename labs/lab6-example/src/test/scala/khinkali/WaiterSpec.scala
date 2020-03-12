package khinkali

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Chef.TakeOrder
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class AsyncWaiterSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  val testKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "waiter" should "give order to chef upon receiving" in {
    val order    = CustomerOrder(List())
    val chef     = testKit.createTestProbe[Chef.Command]()
    val waiter   = testKit.spawn(Waiter(List(chef.ref)))
    val customer = testKit.createTestProbe[Customer.Command]()
    waiter ! Waiter.ReceiveOrder(order, customer.ref)
    chef.expectMessage(Chef.TakeOrder(order.toOrder(0), waiter))
  }

  "waiter" should "give order to next chef if first unavailable" in {
    val order = CustomerOrder(List())
    val mockedChef = testKit.spawn(Behaviors.receiveMessage[TakeOrder] { msg =>
      msg.replyTo ! Waiter.ChefResponse(Chef.OrderStatus.Rejected(msg.order))
      Behaviors.same
    })

    val chef     = testKit.createTestProbe[Chef.Command]()
    val waiter   = testKit.spawn(Waiter(List(mockedChef.ref, chef.ref)))
    val customer = testKit.createTestProbe[Customer.Command]()
    waiter ! Waiter.ReceiveOrder(order, customer.ref)
    chef.expectMessage(Chef.TakeOrder(order.toOrder(0), waiter))
  }

  "waiter" should "not give order to next chef if first was available" in {
    val order = CustomerOrder(List())
    val mockedChef = testKit.spawn(Behaviors.receiveMessage[TakeOrder] { msg =>
      msg.replyTo ! Waiter.ChefResponse(Chef.OrderStatus.Accepted(msg.order))
      Behaviors.same
    })

    val chef     = testKit.createTestProbe[Chef.Command]()
    val waiter   = testKit.spawn(Waiter(List(mockedChef.ref, chef.ref)))
    val customer = testKit.createTestProbe[Customer.Command]()
    waiter ! Waiter.ReceiveOrder(order, customer.ref)
    chef.expectNoMessage()
  }

  "waiter" should "should serve order when chef finishes" in {
    val order    = CustomerOrder(List())
    val chef     = testKit.createTestProbe[Chef.Command]()
    val waiter   = testKit.spawn(Waiter(List(chef.ref)))
    val customer = testKit.createTestProbe[Customer.Command]()
    waiter ! Waiter.ReceiveOrder(order, customer.ref)
    waiter ! Waiter.ChefResponse(Chef.OrderStatus.Accepted(order.toOrder(0)))
    waiter ! Waiter.ChefResponse(Chef.OrderStatus.Finished(order.toOrder(0)))
    customer.expectMessage(Customer.Eat)
  }

}
