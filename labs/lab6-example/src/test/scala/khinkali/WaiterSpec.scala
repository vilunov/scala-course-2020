package khinkali

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WaiterSpec extends AnyFlatSpec with Matchers {
  import WaiterSpec._

  "Waiter" should "correctly accept order" in {
    // Arrange
    val waiter = BehaviorTestKit(Waiter(Vector.empty))
    val customer = BehaviorTestKit(Customer(waiter.ref, order, gen.ref, config.waitingTime))
    waiter.run(Waiter.AcceptOrder(order, customer.ref))

    // Act
    val state = waiter.selfInbox().receiveAll().headOption
    val correctState = state match {
      case Some(Waiter.ProcessOrder(order, _)) => order.orderId == 0
      case None                                => false
    }

    // Assert
    correctState shouldEqual true
  }

  "Waiter" should "reveal order to chef correctly" in {
    // Arrange
    val actorTestKit: ActorTestKit = ActorTestKit()
    val probe = actorTestKit.createTestProbe[Chef.Command]()
    val waiter = actorTestKit.spawn(Waiter.acceptOrder(Vector(probe.ref), 0))
    val customer = actorTestKit.createTestProbe[Customer.Command]()

    // Act
    waiter ! Waiter.AcceptOrder(order, customer.ref)
    val message = probe.receiveMessage()

    // Assert
    message shouldBe a [Chef.AcceptOrder]
    actorTestKit.shutdownTestKit()
  }
}

object WaiterSpec {
  private val config = CafeConfig(5, 5, (2, 2), List(BeefTime(1, 3), ChickenTime(2, 4), VeganTime(1, 5)), 5, 42)
  private val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 5), Khinkali(Stuffing.Vegan, 10)))
  val gen: BehaviorTestKit[RandomnessManager.Command] = BehaviorTestKit(RandomnessManager(config.seed))
}