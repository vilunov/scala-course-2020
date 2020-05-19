package khinkali

import Chef.TakeOrder
import akka.actor.testkit.typed.Effect
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.flatspec.AnyFlatSpec
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration._
import scala.util.Random

class ChefSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  "Chef" should "accept order" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter      = TestInbox[Waiter.Command]()
        val behaviorKit = BehaviorTestKit(Chef(waiter.ref, 777, config.chef))
        val order       = CustomerOrder(List(Khinkali(Stuffing.Beef, 100))).toOrder(0)
        val testInbox   = TestInbox[Result]()
        behaviorKit.run(TakeOrder(order, testInbox.ref))
        testInbox.expectMessage(Result.Ok)
      case Left(e) => println(e)
    }
  }
  "Chef" should "decline order" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter      = TestInbox[Waiter.Command]()
        val behaviorKit = BehaviorTestKit(Chef(waiter.ref, 777, config.chef))
        val order       = CustomerOrder(List(Khinkali(Stuffing.Beef, 100))).toOrder(0)
        val testInbox   = TestInbox[Result]()
        behaviorKit.run(TakeOrder(order, testInbox.ref))
        testInbox.expectMessage(Result.Ok)
        behaviorKit.run(TakeOrder(order, testInbox.ref))
        testInbox.expectMessage(Result.Busy)
      case Left(e) => println(e)
    }
  }
  "Chef" should "schedule cooking" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter      = TestInbox[Waiter.Command]()
        val behaviorKit = BehaviorTestKit(Chef(waiter.ref, 777, config.chef))
        val order       = CustomerOrder(List(Khinkali(Stuffing.Beef, 100))).toOrder(0)
        val testInbox   = TestInbox[Result]()
        behaviorKit.run(TakeOrder(order, testInbox.ref))
        testInbox.expectMessage(Result.Ok)
        val time = Chef.computeTime(order, new Random(777), config.chef)
        behaviorKit.expectEffect(Effect.Scheduled(time, behaviorKit.ref, Chef.FinishOrder))
      case Left(e) => println(e)
    }
  }
  "Chef" should "pass finished order to waiter" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter      = TestInbox[Waiter.Command]()
        val behaviorKit = BehaviorTestKit(Chef(waiter.ref, 777, config.chef))
        val order       = CustomerOrder(List(Khinkali(Stuffing.Beef, 100))).toOrder(0)
        val testInbox   = TestInbox[Result]()
        behaviorKit.run(TakeOrder(order, testInbox.ref))
        testInbox.expectMessage(Result.Ok)
        behaviorKit.run(Chef.FinishOrder)
        waiter.expectMessage(Waiter.ServeOrder(order.orderId))
      case Left(e) => println(e)
    }
  }
  "Chef" should "compute big order time correctly" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 100))).toOrder(0)
        val time  = Chef.computeTime(order, new Random(777), config.chef)
        time shouldBe 73.311798096.second
      case Left(e) => println(e)
    }
  }
  "Chef" should "compute order time correctly" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val order = CustomerOrder(List(Khinkali(Stuffing.Beef, 10))).toOrder(0)
        val time  = Chef.computeTime(order, new Random(777), config.chef)
        time shouldBe 7.483309746.second
      case Left(e) => println(e)
    }
  }
}
