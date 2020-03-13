package khinkali

import akka.actor.testkit.typed.Effect.Scheduled
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
import khinkali.Stuffing.CheeseAndMushrooms
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.flatspec.AnyFlatSpec
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.duration._
import scala.util.Random

class CustomerSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  "Customer" should "leave order" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter        = TestInbox[Waiter.Command]()
        val kit           = BehaviorTestKit(Customer(waiter.ref, config.seed, config.customer))
        val random        = new Random(config.seed)
        val order         = Customer.generateOrder(random, config.customer)
        val ordering_time = Utils.randomRange(random, config.customer.orderingTime)
        kit.run(Customer.Start)
        kit.expectEffect(Scheduled(ordering_time.second, kit.ref, Customer.LeaveOrder(order)))
      case Left(e) => println(e)
    }
  }
  "Customer" should "generate order" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val order    = Customer.generateOrder(new Random(config.seed), config.customer)
        val expected = CustomerOrder(List(Khinkali(CheeseAndMushrooms, 4), Khinkali(CheeseAndMushrooms, 7)))
        order shouldBe expected
      case Left(e) => println(e)
    }
  }
  "Customer" should "wait to eat" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter = TestInbox[Waiter.Command]()
        val kit    = BehaviorTestKit(Customer(waiter.ref, config.seed, config.customer))
        val random = new Random(config.seed)
        val order  = Customer.generateOrder(random, config.customer)
        kit.run(Customer.Start)
        kit.run(Customer.LeaveOrder(order))
        waiter.expectMessage(Waiter.TakeOrder(kit.ref, order))
      case Left(e) => println(e)
    }
  }
  "Customer" should "wait to leave" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter        = TestInbox[Waiter.Command]()
        val kit           = BehaviorTestKit(Customer(waiter.ref, config.seed, config.customer))
        val random        = new Random(config.seed)
        val order         = Customer.generateOrder(random, config.customer)
        val ordering_time = Utils.randomRange(random, config.customer.orderingTime)
        val waiting_time  = Utils.randomRange(random, config.customer.eatingTime)
        kit.run(Customer.Start)
        kit.run(Customer.LeaveOrder(order))
        kit.expectEffect(Scheduled(ordering_time.second, kit.ref, Customer.LeaveOrder(order)))
        kit.run(Customer.Eat)
        kit.expectEffect(Scheduled(waiting_time.second, kit.ref, Customer.Leave))
      case Left(e) => println(e)
    }
  }
  "Customer" should "stop after leave" in {
    ConfigSource.file("src/test/resources/application.conf").load[CafeConfig] match {
      case Right(config) =>
        val waiter = TestInbox[Waiter.Command]()
        val kit    = BehaviorTestKit(Customer(waiter.ref, config.seed, config.customer))
        val random = new Random(config.seed)
        val order  = Customer.generateOrder(random, config.customer)
        kit.run(Customer.Start)
        kit.run(Customer.LeaveOrder(order))
        kit.run(Customer.Eat)
        kit.run(Customer.Leave)
        kit.isAlive shouldBe false
      case Left(e) => println(e)
    }
  }
}
