package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, ActorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.{BeforeAndAfterAll, Matchers}

import scala.concurrent.duration._
import scala.util.Random


class CustomerSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  // spasibo Egor za help s ActorTestKit
  val rngesus = new Random(1) // same randomness every time
  val testKit: ActorTestKit = ActorTestKit("myKit")

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "Customer" should "Make orders" in {

    val order = CustomerOrder(List())
    val testWaiter = testKit.createTestProbe[Waiter.Command]()
    val cafeInbox = testKit.createTestProbe[Cafe.Command]()
    val customer = testKit.spawn(
      Customer(0, testWaiter.ref, cafeInbox.ref,
        CustomerConfig(SelectingTime(1, 0), EatingTime(1, 0)),
        OrderConfig(OrderedDishes(0, 0), KhinkalisInDish(0, 0))),
      "customer"
    )
    customer ! Customer.Start
    testWaiter.expectMessage(Waiter.TakeOrder(customer.ref, order))

  }

  "Customer" should "Eat then leave" in {
    val order = CustomerOrder(List())
    val testWaiter = testKit.createTestProbe[Waiter.Command]()
    val cafeInbox = testKit.createTestProbe[Cafe.Command]()
    val customer = testKit.spawn(
      new Customer(testWaiter.ref, cafeInbox.ref, new Random(0),
        CustomerConfig(SelectingTime(1, 0), EatingTime(1, 0))
      ).waitForEat,
      "customer2"
    )

    customer ! Customer.Eat

    cafeInbox.expectTerminated(customer)
  }
}
