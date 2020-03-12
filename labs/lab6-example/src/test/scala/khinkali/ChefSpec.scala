package khinkali

  import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}
  import org.scalatest.flatspec.AnyFlatSpec
  import org.scalatest.matchers.should.Matchers

  class ChefSpec extends AnyFlatSpec with Matchers {
    import ChefSpec._

    "Chef" should "accept order when ready" in {
      val inbox = TestInbox[Result]()
      val chef = BehaviorTestKit(Chef(cfg.cookingTimes, gen.ref))
      val waiter = BehaviorTestKit(Waiter(Vector(chef.ref)))
      val customerOrder = CustomerOrder(List(Khinkali(Stuffing.Beef, 1)))
      val customer = BehaviorTestKit(Customer(waiter.ref, customerOrder, gen.ref, cfg))

      chef.run(Chef.TakeOrder(customerOrder.toOrder(0), customer.ref, inbox.ref))
      val messages = inbox.receiveAll()
      messages shouldEqual Seq(Result.Ok)
    }

  "Chef" should "not accept order when already making another one" in {
    val inbox = TestInbox[Result]()
    val chef = BehaviorTestKit(Chef(cfg.cookingTimes, gen.ref))
    val waiter = BehaviorTestKit(Waiter(Vector(chef.ref)))
    val customerOrder = CustomerOrder(List(Khinkali(Stuffing.Mutton, 1)))
    val customer = BehaviorTestKit(Customer(waiter.ref, customerOrder, gen.ref, cfg))

    chef.run(Chef.TakeOrder(customerOrder.toOrder(0), customer.ref, inbox.ref))
    chef.run(Chef.TakeOrder(customerOrder.toOrder(1), customer.ref, inbox.ref))
    val messages = inbox.receiveAll()
    messages shouldEqual Seq(Result.Ok, Result.Busy)
  }

  "Chef" should "start cooking order if ready" in {
    val inbox = TestInbox[Result]()
    val chef = BehaviorTestKit(Chef(cfg.cookingTimes, gen.ref))
    val waiter = BehaviorTestKit(Waiter(Vector(chef.ref)))
    val customerOrder = CustomerOrder(
      List(Khinkali(Stuffing.CheeseAndMushrooms, 1), Khinkali(Stuffing.Beef, 2))
    )
    val customer = BehaviorTestKit(Customer(waiter.ref, customerOrder, gen.ref, cfg))

    chef.run(Chef.TakeOrder(customerOrder.toOrder(0), customer.ref, inbox.ref))
    val correctState = chef.selfInbox().receiveAll().headOption match {
      case Some(Chef.Cooking(order, _)) => order.orderId == 0
      case None => false
    }
    correctState shouldEqual true
  }

}

object ChefSpec {
  val cfg: CafeConfig = CafeConfig(
    customersCnt = 4,
    chefsCnt = 2,
    waitingTime = (1, 2),
    eatingTime = (2, 3),
    cookingTimes = List(BeefTime(1, 2), MuttonTime(2, 3), CheeseAndMushroomsTime(3, 4))
  )
  val gen: BehaviorTestKit[RandomGenerator.Command] = BehaviorTestKit(RandomGenerator())
}