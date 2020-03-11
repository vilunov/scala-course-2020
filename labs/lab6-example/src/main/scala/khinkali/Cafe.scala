package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Cafe {
  sealed trait Command
  case object Start extends Command
  case object Callback extends Command
  def generateOrder: List[Khinkali] = {
    List(Khinkali(Stuffing.Beef, 7), Khinkali(Stuffing.Mutton, 3))
  }
  def apply(n_customers: Int,
            n_chefs: Int,
            callbacked: Int = 1,
            startupTime: Long = System.currentTimeMillis()): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val chefs = (1 to n_chefs).map { i =>
            ctx.spawn(Chef(), s"Chef$i")
          }
          val waiter = ctx.spawn(Waiter(chefs), "Waiter")
          val customers = (1 to n_customers).map { i =>
            ctx.spawn(
              Customer(waiter, CustomerOrder(generateOrder)),
              s"Customer$i"
            )
          }

          waiter ! Waiter.Start

          customers.foreach { c =>
            c ! Customer.Start(ctx.self)
          }
          Behaviors.same
        case Callback =>
          if (callbacked < Config.customerConfig.N) {
            apply(-1, -1, callbacked + 1, startupTime)
          } else {
            val duration = (System.currentTimeMillis() - startupTime) / 1000.0
            ctx.log.info(s"Overall time taken: $duration seconds")
            Behaviors.stopped
          }
      }
    }
}
