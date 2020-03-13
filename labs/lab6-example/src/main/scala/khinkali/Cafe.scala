package khinkali

import java.time._
import java.time.temporal._

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

import scala.util.Random

object Cafe {
  sealed trait Command
  case object Start      extends Command
  case object Terminated extends Command

  def apply(config: CafeConfig): Behavior[Command] =
    run(LocalDateTime.now(), config, config.numCustomers)

  def run(started: LocalDateTime, config: CafeConfig, leftCustomers: Int): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      msg match {
        case Start =>
          val random = new Random(config.seed)
          val waiter = ctx.spawn(Waiter(), "Waiter")
          val chefs = (1 to config.numChefs).map { i =>
            ctx.spawn(Chef(waiter, random.nextLong(), config.chef), s"Chef$i")
          }
          val customers = (1 to config.numCustomers).map { i =>
            ctx.spawn(Customer(waiter, random.nextLong(), config.customer), s"Customer$i")
          }

          waiter ! Waiter.Start(chefs.toList)
          customers.foreach { c => ctx.watchWith(c, Terminated) }
          customers.foreach { c => c ! Customer.Start }
          Behaviors.same
        case Terminated =>
          leftCustomers match {
            case x if x <= 1 =>
              val time = started.until(LocalDateTime.now(), ChronoUnit.SECONDS)
              ctx.log.info(s"All customers left, took $time seconds")
              Behaviors.stopped
            case _ => run(started, config, leftCustomers - 1)
          }
      }
    }

}
