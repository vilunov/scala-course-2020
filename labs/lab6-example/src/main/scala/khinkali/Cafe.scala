package khinkali

import java.sql.Timestamp

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{Behaviors, Routers}
import khinkali.Stuffing.{Beef, CheeseAndMushrooms, Mutton}

import scala.concurrent.duration._


object Cafe {

  sealed trait Command

  case object Start extends Command

  case object CustomerLeave extends Command

  def apply(): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val pool = Routers.pool(poolSize = 3)(Chef())
        val router = ctx.spawn(pool, "Chef-pool")

        val waiter = ctx.spawn(Waiter(router), "Waiter")
        val menu = Menu(List(Khinkali(Beef, _), Khinkali(CheeseAndMushrooms, _), Khinkali(Mutton, _)))
        // TODO: parametrise
        val customers = (1 to 10).map { i => ctx.spawn(Customer(waiter, menu), s"Customer$i") }

        customers.foreach { c =>
          c ! Customer.Start
          ctx.watchWith(c, CustomerLeave)
        }
        waitTillAllEat(System.nanoTime(), customers.length)
    }
  }

  def waitTillAllEat(startTime: Long, countDown: Int): Behavior[Command] =
    Behaviors.receive {
      (_, msg) =>
        msg match {
          case CustomerLeave =>
            val newCounter = countDown - 1
            if (newCounter <= 0) {
              println(s"Time taken: ${Duration(System.nanoTime() - startTime, NANOSECONDS).toSeconds} sec.")
              Behaviors.stopped
            }
            else
              waitTillAllEat(startTime, newCounter)
        }
    }
}

