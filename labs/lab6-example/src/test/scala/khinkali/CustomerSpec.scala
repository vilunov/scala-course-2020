package khinkali

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit}
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.duration._

class CustomerSpec extends AnyFlatSpec {
  import CustomerSpec._

  "Customer" should "make order and send it to waiter" in {
    // Arrange
    val actorTestKit: ActorTestKit = ActorTestKit()
    val waiter = actorTestKit.createTestProbe[Waiter.Command]()
    val gen = actorTestKit.spawn(RandomnessManager(config.seed))
    val customer = actorTestKit.spawn(Customer(waiter.ref, order, gen.ref, config.waitingTime))

    // Act
    customer ! Customer.Start

    // Assert
    waiter.expectMessage(Waiter.AcceptOrder(order, customer))
    actorTestKit.shutdownTestKit()
  }

  "Customer" should "lbe terminated after eating" in {
    // Arrange
    val actorTestKit: ActorTestKit = ActorTestKit()
    val probe = actorTestKit.createTestProbe()
    val customer = actorTestKit.spawn(Customer.waitForEat(gen.ref, config.waitingTime))

    // Act
    customer ! Customer.Eat

    // Assert
    probe.expectTerminated(customer, FiniteDuration(5, "s"))
    actorTestKit.shutdownTestKit()
  }

}

object CustomerSpec {
  private val config = CafeConfig(5, 5, (2, 2), List(BeefTime(1, 3), ChickenTime(2, 4), VeganTime(1, 5)), 5, 42)
  val gen: BehaviorTestKit[RandomnessManager.Command] = BehaviorTestKit(RandomnessManager(config.seed))
  private val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 5), Khinkali(Stuffing.Vegan, 10)))
}