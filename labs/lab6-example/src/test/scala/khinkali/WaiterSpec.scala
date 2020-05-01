package khinkali

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class WaiterSpec extends AnyFlatSpec with Matchers {
  import WaiterSpec._

  "Waiter" should "accept incoming order" in {
    val waiter = BehaviorTestKit(Waiter(Vector.empty))
    val customerOrder = CustomerOrder(List(Khinkali(Stuffing.Mutton, 1)))
    val customer = BehaviorTestKit(Customer(waiter.ref, customerOrder, gen.ref, cfg))
    waiter.run(Waiter.AcceptOrder(customerOrder, customer.ref))

    val correctState = waiter.selfInbox().receiveAll().headOption match {
      case Some(Waiter.ProcessOrder(order, _)) => order.orderId == 0
      case None                                => false
    }

    correctState shouldEqual true
  }

}

class AsyncWaiterSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  val kit: ActorTestKit = ActorTestKit()
  override def afterAll(): Unit = kit.shutdownTestKit()

  "Waiter" should "search for chef to cook the order" in {

    val chefProbe = kit.createTestProbe[Chef.Command]()
    val waiter = kit.spawn(Waiter.acceptOrder(Vector(chefProbe.ref), 0))
    val customerOrder = CustomerOrder(List(Khinkali(Stuffing.Beef, 2)))
    val customer = kit.createTestProbe[Customer.Command]()

    waiter ! Waiter.AcceptOrder(customerOrder, customer.ref)
    val message = chefProbe.receiveMessage()
    val messageOk = message match {
      case Chef.TakeOrder(order, _, _) => order.orderId == 0
      case _                           => false
    }

    messageOk shouldEqual true
  }

}

object WaiterSpec {
  val cfg: CafeConfig = CafeConfig(
    customersCnt = 4,
    chefsCnt = 2,
    waitingTime = (1, 2),
    eatingTime = (2, 3),
    cookingTimes = List(BeefTime(1, 2), MuttonTime(2, 3), CheeseAndMushroomsTime(3, 4))
  )
  val gen: BehaviorTestKit[RandomGenerator.Command] = BehaviorTestKit(RandomGenerator())
}