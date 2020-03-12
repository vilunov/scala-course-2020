package khinkali

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import akka.actor.testkit.typed.scaladsl.{ActorTestKit}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._
import scala.util.Random

class CustomerSpec extends AnyFlatSpec with Matchers {
  "order" should "be correctly generated" in {
    val random = new Random()
    val order  = Customer.decide(Range(3, 8), random, Map())
    order.dishes.length should (be >= (3) and be < (8))
  }
}

class AsyncCustomerSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  val testKit = ActorTestKit()

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "customer" should "leave order" in {
    val order     = CustomerOrder(List())
    val waiter    = testKit.createTestProbe[Waiter.Command]()
    val cafeInbox = testKit.createTestProbe[Cafe.Command]()
    val customer  = testKit.spawn(Customer(waiter.ref, order, 0.seconds, 0.seconds, cafeInbox.ref), "customer")
    customer ! Customer.Start
    waiter.expectMessage(Waiter.ReceiveOrder(order, customer.ref))
  }

  "customer" should "leave after eating" in {
    val cafeInbox = testKit.createTestProbe[Cafe.Command]()
    val customer  = testKit.spawn(Customer.waitForEat(0.seconds, cafeInbox.ref), "customer1")
    customer ! Customer.Eat
    cafeInbox.expectMessage(Cafe.CustomerLeft)
  }
}
