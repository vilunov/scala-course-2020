package khinkali

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.concurrent.duration._

class CustomerSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  import CustomerSpec._

  val kit: ActorTestKit = ActorTestKit()
  override def afterAll(): Unit = kit.shutdownTestKit()

  "Customer" should "leave order after coming in the cafe" in {
    val waiter = kit.createTestProbe[Waiter.Command]()
    val customerOrder = CustomerOrder(
      List(Khinkali(Stuffing.CheeseAndMushrooms, 1), Khinkali(Stuffing.Beef, 2))
    )
    val gen = kit.spawn(RandomGenerator())
    val customer = kit.spawn(Customer(waiter.ref, customerOrder, gen.ref, cfg))
    customer ! Customer.Start
    waiter.expectMessage(Waiter.AcceptOrder(customerOrder, customer))
  }

  "Customer" should "leave cafe after eating" in {
    val probe = kit.createTestProbe()
    val gen = kit.spawn(RandomGenerator())
    val customer = kit.spawn(Customer.waitForEat(gen.ref, cfg))
    customer ! Customer.Eat
    probe.expectTerminated(customer, FiniteDuration(10, "s"))
  }

}

object CustomerSpec {
  val cfg: CafeConfig = CafeConfig(
    customersCnt = 4,
    chefsCnt = 2,
    waitingTime = (0, 0.1),
    eatingTime = (0, 0.1),
    cookingTimes = List(BeefTime(0, 0.1), MuttonTime(0, 0.1), CheeseAndMushroomsTime(0, 0.1))
  )
}
