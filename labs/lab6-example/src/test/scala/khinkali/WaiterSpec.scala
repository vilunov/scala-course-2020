package khinkali

import org.scalatest.flatspec.AnyFlatSpec
import khinkali.Chef._
import khinkali.Stuffing.CheeseAndMushrooms
import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.Effect.Scheduled
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import akka.actor.testkit.typed.scaladsl.TestInbox
import akka.actor.typed.Behavior
import org.slf4j.event.Level


class WaiterSpec extends AnyFlatSpec with BeforeAndAfterAll with Matchers {

  import WaiterSpec._

  "waiter" should "take order when chef is free" in {
    val freeChef = BehaviorTestKit(getChef)
    val waiter = BehaviorTestKit(Waiter(freeChef.ref, waiterConf))
    val custName = "FakeCustomer"
    val customerBox = TestInbox[Customer.Command](custName)
    waiter.run(Waiter.ReceiveOrder(order, customerBox.ref))

    waiter.logEntries() shouldBe
      Seq(CapturedLogEvent(Level.INFO, Waiter.OrderRegisteredMessage(Waiter.counterStart, custName)))
    println(waiter.retrieveAllEffects())
    println(freeChef.retrieveAllEffects())
  }

}

object WaiterSpec {
  def getChef: Behavior[Command] = Chef.freeState(ChefSpec.chefConf)

  val waiterConf = WaiterConf(1, 1)
  val order = CustomerOrder(List(Khinkali(CheeseAndMushrooms, 1)))
}