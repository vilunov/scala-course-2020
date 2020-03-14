package khinkali

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import khinkali.Waiter.{ChefResult, ServeOrder, TakeOrder}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WaiterSpec extends AnyFlatSpec with Matchers with BeforeAndAfterAll {

  "Waiter" should "accept TakeOrder correct" in {
    val chefInbox = TestInbox[Chef.Command]()
    val testKit = BehaviorTestKit(Waiter(List(chefInbox.ref)))
    val inbox = TestInbox[Customer.Command]()
    val orders = CustomerOrder(List(Khinkali(Stuffing.Beef, 5)))
    testKit.run(TakeOrder(orders, inbox.ref))
    chefInbox.receiveMessage() shouldBe a[Chef.TakeOrder]
  }

  val testKit: ActorTestKit = ActorTestKit()

  override protected def afterAll(): Unit = testKit.shutdownTestKit()

  "Waiter" should "accept ServeOrder correct" in {
    val chefInbox = TestInbox[Chef.Command]()
    val testKit = BehaviorTestKit(Waiter(List(chefInbox.ref)))
    val customerInbox = TestInbox[Customer.Command]()
    val orders = CustomerOrder(List(Khinkali(Stuffing.Beef, 5)))
    testKit.run(TakeOrder(orders, customerInbox.ref))
    chefInbox.receiveMessage() shouldBe a[Chef.TakeOrder]

    testKit.run(ServeOrder(0))
    customerInbox.receiveMessage() shouldBe Customer.Eat
  }

  "Waiter" should "resend TakeOrder to Chef" in {
    val chef1 = TestInbox[Chef.Command]()
    val chef2 = TestInbox[Chef.Command]()
    val waiter = BehaviorTestKit(Waiter(List(chef1.ref, chef2.ref)))
    val customerInbox = TestInbox[Customer.Command]()
    val customerInbox2 = TestInbox[Customer.Command]()
    val orders = CustomerOrder(List(Khinkali(Stuffing.Beef, 5)))
    val orders2 = CustomerOrder(List(Khinkali(Stuffing.Beef, 5)))
    waiter.run(TakeOrder(orders, customerInbox.ref))
    chef1.receiveMessage() shouldBe a[Chef.TakeOrder]

    waiter.run(TakeOrder(orders2, customerInbox2.ref))
    chef1.receiveMessage() shouldBe a[Chef.TakeOrder]

    waiter.run(ChefResult(Result.Busy(Order(0, orders2.dishes), List(chef2.ref))))
    chef2.receiveMessage() shouldBe a[Chef.TakeOrder]
  }
}
