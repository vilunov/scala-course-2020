package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.util.Random

object Cafe {
  sealed trait Command
  case object Start extends Command
  case object Callback extends Command
  def generateOrder: List[Khinkali] = {
    List(Khinkali(Stuffing.Beef, 7), Khinkali(Stuffing.Mutton, 3))
  }
  def apply(cfg: Config,
            callbacked: Int = 1,
            startupTime: Long = System.currentTimeMillis()): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val rng = new Random(cfg.seed)
          val chefs = (1 to cfg.chefs.n).map { i =>
            ctx.spawn(Chef(cfg, rng), s"Chef$i")
          }
          val waiter = ctx.spawn(Waiter(chefs, rng), "Waiter")
          val customers = (1 to cfg.customers.n).map { i =>
            ctx.spawn(
              Customer(waiter, CustomerOrder(generateOrder), cfg, rng),
              s"Customer$i"
            )
          }

          waiter ! Waiter.Start

          customers.foreach { c =>
            c ! Customer.Start(ctx.self)
          }
          Behaviors.same
        case Callback =>
          if (callbacked < cfg.customers.n) {
            apply(cfg, callbacked + 1, startupTime)
          } else {
            val duration = (System.currentTimeMillis() - startupTime) / 1000.0
            ctx.log.info(s"Overall time taken: $duration seconds")
            Behaviors.stopped
          }
      }
    }
}
