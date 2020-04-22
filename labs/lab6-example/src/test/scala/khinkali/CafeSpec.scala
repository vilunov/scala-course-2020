package khinkali

import akka.actor.testkit.typed.Effect.Spawned
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import pureconfig.ConfigSource

import scala.concurrent.duration._
import pureconfig.generic.auto._


class CafeSpec extends AnyFlatSpec with Matchers with OptionValues {
  import CafeSpec._

  "Customer" should "choose and make an order" in {
    val config = ConfigSource.string(configString).load[Config].toOption.value
    val testKit = BehaviorTestKit(Cafe(config))
    testKit.run(Cafe.Start)
    val spawnedActors = testKit.retrieveAllEffects().collect {
      case Spawned(_, name, _) => name;
    }
    spawnedActors.count(_.contains(Constants.customer)) shouldBe config.numberOfCustomers
    spawnedActors.count(_.contains(Constants.chef)) shouldBe config.numberOfChefs
    spawnedActors.count(_.contains(Constants.waiter)) shouldBe 1
  }
}
object CafeSpec {
  val configString = "number-of-chefs = 10\nnumber-of-customers = 2\nseed = 209384\nchef-config = { type = \"chef-config\",  cooking-time-boundaries = { type = \"boundaries\", min = 1, max = 2 }}\ncustomer-config = { type = \"customer-config\",\n  order-decision-time-boundaries = { type = \"boundaries\", min = 1, max = 2 },\n  eating-time-boundaries = { type = \"boundaries\", min = 1, max = 2 },\n  order-size-boundaries: { type = \"boundaries\", min = 1, max = 2 },\n  khinkali-amount-boundaries = { type = \"boundaries\", min = 1, max = 2 }\n}"
}