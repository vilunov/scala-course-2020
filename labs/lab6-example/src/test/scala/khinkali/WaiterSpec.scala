package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.{BeforeAndAfterAll, Matchers}

import scala.concurrent.duration._

class WaiterSpec extends AnyFlatSpec  with BeforeAndAfterAll with Matchers {
  "Customer" should "Make orders" in {
    val chef1 = TestInbox[Chef.Command]()
    val chef2 = TestInbox[Chef.Command]()
    val testCustomer = TestInbox[Customer.Command]()
    val testWaiter = TestInbox[Result]()
    val customerOrder = CustomerOrder(List(Khinkali(Stuffing.Beef, 10)))

    val behaviourKit = BehaviorTestKit(Waiter(IndexedSeq(chef1.ref, chef2.ref)))

    //кароче я хз как это тестить, ни то ни другое не работает

    //behaviourKit.run(Waiter.ReceiveOrder(customerOrder, testCustomer.ref))
    //behaviourKit.run(Waiter.Ask(customerOrder.toOrder(0), 0, testCustomer.ref))

    //chef1.expectMessage(Chef.TakeOrder(customerOrder.toOrder(0), testWaiter.ref, testCustomer.ref))
  }
}
