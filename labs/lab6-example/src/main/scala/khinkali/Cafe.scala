package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.util.Random
import scala.concurrent.duration._

object Cafe {

  sealed trait Command

  case object Start extends Command
  case object CustomerTerminated extends Command

  val startupTime: Long = System.currentTimeMillis()

  var remainingCustomers: Int = _

  def apply(cafeConf: cafeConf): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val rngesus = new Random(cafeConf.randomSeed)

        val chefs = (1 to cafeConf.nChefs).map {
          i => ctx.spawn(Chef(rngesus.nextInt(), cafeConf.cookingTime), s"Chef$i")
        }
        val waiter = ctx.spawn(Waiter(chefs), "Waiter")
        remainingCustomers = cafeConf.nCustomers
        val customers = (1 to cafeConf.nCustomers).map {
          i => {
            ctx.spawn(
              Customer(rngesus.nextInt(), waiter, ctx.self, cafeConf.customerConfig, cafeConf.orderConfig),
              s"Customer$i"
            )
          }
        }


        customers.foreach { c => c ! Customer.Start }
        Behaviors.same

      case CustomerTerminated => // идея заоперсурсена у данилы
        remainingCustomers -= 1
        if (remainingCustomers == 0) {
          val timeTaken = ((System.currentTimeMillis() - startupTime).toFloat / 1000f)
          ctx.log.info(s"CAFE DONE. Time taken: $timeTaken seconds")
          Behaviors.stopped
        } else {
          Behaviors.same
        }
      case _ => Behaviors.same
    }
  }
}
