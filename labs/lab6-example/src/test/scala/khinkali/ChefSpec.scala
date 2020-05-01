package khinkali

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChefSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  import ChefSpec._

  val actorTestKit: ActorTestKit = ActorTestKit()
  override def afterAll(): Unit = actorTestKit.shutdownTestKit()

  "Chef" should "accept order correctly" in {
    // Arrange
    val inbox = TestInbox[Result]()
    val chef = BehaviorTestKit(Chef(config.cookingTimes, gen.ref))
    val waiter = BehaviorTestKit(Waiter(Vector(chef.ref)))
    val customer = BehaviorTestKit(Customer(waiter.ref, order, gen.ref, config.waitingTime))

    // Act
    chef.run(Chef.AcceptOrder(order.toOrder(0), customer.ref, inbox.ref))
    val messages = inbox.receiveAll()

    // Assert
    messages shouldEqual Seq(Result.Ok)
  }

  "Chef" should "start cooking correctly" in {
    // Arrange
    val inbox = TestInbox[Result]()
    val chef = BehaviorTestKit(Chef(config.cookingTimes, gen.ref))
    val waiter = BehaviorTestKit(Waiter(Vector(chef.ref)))
    val customer = BehaviorTestKit(Customer(waiter.ref, order, gen.ref, config.waitingTime))

    // Act
    chef.run(Chef.AcceptOrder(order.toOrder(0), customer.ref, inbox.ref))
    val cookingEffect = chef.retrieveEffect().toString
    val correctCooing = cookingEffect.contains(order.dishes.toString)

    // Assert
    correctCooing shouldEqual true
  }
}

object ChefSpec {
  private val config = CafeConfig(5, 5, (2, 2), List(BeefTime(1, 3), ChickenTime(2, 4), VeganTime(1, 5)), 5, 42)
  private val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 5), Khinkali(Stuffing.Vegan, 10)))
  private val gen: BehaviorTestKit[RandomnessManager.Command] = BehaviorTestKit(RandomnessManager(config.seed))
}