package khinkali

import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class CafeSpec extends AnyFlatSpec with Matchers {
  "Cafe" should "stop when all customers left" in {
    val testKit = BehaviorTestKit(Cafe.countTime(1, 0.seconds), "cafe")
    testKit.run(Cafe.CustomerLeft)
    testKit.isAlive shouldBe false
  }
}
