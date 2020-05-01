package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CafeSpec extends AnyFlatSpec with Matchers {
  import CafeSpec._

  "cafe" should "start with correct amount of employees and clients" in {
    val kit = BehaviorTestKit(Cafe(cfg))
    kit.run(Cafe.Start)

    val spawnNames = kit.retrieveAllEffects().collect { case Effect.Spawned(_, name, _) => name }
    val chefsCnt = spawnNames.count(_.contains("Chef"))
    val customersCnt = spawnNames.count(_.contains("Customer"))

    (chefsCnt, customersCnt) shouldEqual (cfg.chefsCnt, cfg.customersCnt)
  }

  "cafe" should "stop when Finish command appears" in {
    val kit = BehaviorTestKit(Cafe(cfg))
    kit.run(Cafe.Start)
    kit.run(Cafe.Finish)
    kit.isAlive shouldEqual false
  }

}

object CafeSpec {
  val cfg: CafeConfig = CafeConfig(
    customersCnt = 4,
    chefsCnt = 2,
    waitingTime = (1, 2),
    eatingTime = (2, 3),
    cookingTimes = List(BeefTime(1, 2), MuttonTime(2, 3), CheeseAndMushroomsTime(3, 4))
  )
}