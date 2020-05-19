package khinkali

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, BehaviorTestKit, TestInbox}
import org.scalatest.{BeforeAndAfterAll, Matchers}
import org.scalatest.flatspec.AnyFlatSpec

class WaiterSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {
  "Waiter" should "accept order" in {
    val kit      = BehaviorTestKit(Waiter())
    val order    = CustomerOrder(List(Khinkali(Stuffing.Beef, 100)))
    val customer = TestInbox[Customer.Command]()
    kit.run(Waiter.Start(Nil))
    kit.run(Waiter.TakeOrder(customer.ref, order))
    kit.selfInbox.expectMessage(Waiter.AssignChef(customer.ref, order.toOrder(0), Nil))
  }
  "Waiter" should "assign chef" in {
    val kit: ActorTestKit = ActorTestKit()
    val customer          = kit.createTestProbe[Customer.Command]()
    val waiter            = kit.spawn(Waiter())
    val chef              = kit.createTestProbe[Chef.Command]()
    val order             = CustomerOrder(List(Khinkali(Stuffing.Beef, 100)))
    waiter ! Waiter.Start(List(chef.ref))
    waiter ! Waiter.TakeOrder(customer.ref, order)
    val Chef.TakeOrder(_, replyTo) = chef.receiveMessage()
    replyTo ! Result.Ok
    waiter ! Waiter.ServeOrder(0)
    customer.expectMessage(Customer.Eat)
    kit.stop(waiter)
    kit.shutdownTestKit()
  }
  "Waiter" should "serve customer" in {
    val kit      = BehaviorTestKit(Waiter())
    val order    = CustomerOrder(List(Khinkali(Stuffing.Beef, 100)))
    val customer = TestInbox[Customer.Command]()
    val chef     = TestInbox[Chef.Command]()
    kit.run(Waiter.Start(List(chef.ref)))
    kit.run(Waiter.TakeOrder(customer.ref, order))
    kit.run(Waiter.ServeOrder(0))
    customer.expectMessage(Customer.Eat)
  }
  "Waiter" should "not lose order" in {
    val kit: ActorTestKit = ActorTestKit()
    val customer          = kit.createTestProbe[Customer.Command]()
    val waiter            = kit.spawn(Waiter())
    val chef              = kit.createTestProbe[Chef.Command]()
    val order             = CustomerOrder(List(Khinkali(Stuffing.Beef, 100)))
    waiter ! Waiter.Start(List(chef.ref))
    waiter ! Waiter.TakeOrder(customer.ref, order)
    val Chef.TakeOrder(_, replyTo1) = chef.receiveMessage()
    replyTo1 ! Result.Busy
    val Chef.TakeOrder(_, replyTo2) = chef.receiveMessage()
    replyTo2 ! Result.Ok
    waiter ! Waiter.ServeOrder(0)
    customer.expectMessage(Customer.Eat)
    kit.stop(waiter)
    kit.shutdownTestKit()
  }
}
