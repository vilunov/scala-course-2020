package khinkali

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.util.Random
import scala.concurrent.duration._

// TODO: System should report the overall taken time to process all customers

object Cafe {

  sealed trait Command

  case object Start extends Command
  case object CustomerTerminated extends Command

  var remainingCustomers: Int = _

  var startupTime: Long = _


  var rngesus: Random = _

  def apply(cafeConf: cafeConf): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        rngesus = new Random(cafeConf.randomSeed)

        val chefs = (1 to cafeConf.nChefs).map {
          i => ctx.spawn(Chef(rngesus, cafeConf.cookingTime), s"Chef$i")
        }
        val waiter = ctx.spawn(Waiter(chefs), "Waiter")
        remainingCustomers = cafeConf.nCustomers
        val customers = (1 to cafeConf.nCustomers).map {
          i => {
            // generate orders
            val nDishes = cafeConf.orderedDishes.avg
                + math.round(cafeConf.orderedDishes.varia
                * (rngesus.nextGaussian() - 0.5f)).toInt

            val order = (1 to nDishes).map {
              _ => {
                val stuffing = rngesus.nextInt().abs % 3 match {
                  case 0 => Stuffing.Beef
                  case 1 => Stuffing.CheeseAndMushrooms
                  case 2 => Stuffing.Mutton
                }
                val amount = cafeConf.khinkalisInDish.avg
                          + math.round(cafeConf.khinkalisInDish.varia
                          * (rngesus.nextGaussian() - 0.5f)).toInt
                Khinkali(stuffing, amount)
              }
            }.toList
            ctx.spawn(Customer(
              ctx.self,
              waiter, CustomerOrder(order),
              rngesus,
              cafeConf.selectingTime, cafeConf.eatingTime
            ), s"Customer$i")
          }
        }

        startupTime = System.currentTimeMillis()

        customers.foreach { c =>
          c ! Customer.Start
        }
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
