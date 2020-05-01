package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.util.{Failure, Success}
import scala.concurrent.duration._

object Cafe {
  sealed trait Command
  case object Proceed extends Command
  case object Start extends Command
  case object Finish extends Command
  case class CustomerLeft(counter: ActorRef[Counter.Command], targetCnt: Int) extends Command

  implicit val timeout: Timeout = Timeout(1.second)

  def apply(cfg: CafeConfig): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val leaveCounter = ctx.spawn(Counter(), "LeaveCounter")
        val randGen = ctx.spawn(RandomGenerator(), "MainGenerator")
        val chefs = (1 to cfg.chefsCnt).map {
          i => ctx.spawn(Chef(cfg.cookingTimes, ctx.spawn(RandomGenerator(), s"Gen$i")), s"Chef$i")
        }.toVector

        val waiter = ctx.spawn(Waiter(chefs), "Waiter")
        val customers = (1 to cfg.customersCnt).map {
          i => ctx.spawn(
            Customer(
              waiter,
              CustomerOrder(List(Khinkali(Stuffing.Beef, 2))),
              randGen,
              cfg
            ), s"Customer$i"
          )
        }

        customers.foreach { c =>
          c ! Customer.Start
          ctx.watchWith(c, CustomerLeft(leaveCounter, cfg.customersCnt))
        }

        Behaviors.same

      case CustomerLeft(counter, targetCnt) =>
        counter ! Counter.Increment
        ctx.ask(counter, Counter.Retrieve) {
          case Success(value) =>
            if (value == targetCnt) {
              println("All customers left, closing the cafe. Congrats!")
              Finish
            } else Proceed
          case Failure(_) => Proceed
        }
        Behaviors.same

      case Finish =>
        Behaviors.stopped

      case _ =>
        Behaviors.same
    }
  }

}
