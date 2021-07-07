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

  def apply(conf: ServiceConf): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val pool = Routers.pool(poolSize = conf.chefsNum)(Chef(conf.chefConf))
        val chefRouter = ctx.spawn(pool, "Chef-pool")

        val waiter = ctx.spawn(Waiter(chefRouter, conf.waiterConf), "Waiter")
        val menu = Menu(List(Khinkali(Beef, _), Khinkali(CheeseAndMushrooms, _), Khinkali(Mutton, _)))
        val customers = (1 to conf.customersNum).map { i => ctx.spawn(Customer(waiter, menu), s"Customer$i") }

        customers.foreach { c =>
          c ! Customer.Start(conf.customerConf)
          ctx.watchWith(c, CustomerLeave)
        }
        waitTillAllEat(System.nanoTime(), customers.length)
      case _ => Behaviors.same
    }
  }

  def waitTillAllEat(startTime: Long, countDown: Int): Behavior[Command] =
    Behaviors.receive {
      (ctx, msg) =>
        msg match {
          case CustomerLeave =>
            val newCounter = countDown - 1
            if (newCounter <= 0) {
              ctx.log.info(s"Time taken: ${Duration(System.nanoTime() - startTime, NANOSECONDS).toSeconds} sec.")
              Behaviors.stopped
            }
            else
              waitTillAllEat(startTime, newCounter)
          case _ => Behaviors.same
        }
    }
}

