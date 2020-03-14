package khinkali

import scala.util.{Failure, Random, Success}
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import khinkali.Stuffing.{Beef, Chicken, Vegan}

object Cafe {
  sealed trait Command
  case object Start extends Command
  case object Stop extends Command
  case class Reply(cnt: Int) extends Command

  def apply(config: CafeConfig): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Start =>
        val randManager = ctx.spawn(RandomnessManager(config.seed), "MyBestGenerator")
        val chefs = (1 to config.chefsCount).map { i =>
          ctx.spawn(Chef(config.cookingTimes, randManager), s"Chef$i")}.toVector

        val waiter = ctx.spawn(Waiter(chefs), "Waiter")

        val customers = (1 to config.guestsCount).map { i =>
          ctx.spawn(
            Customer(
              waiter,
              CustomerOrder(List(generateDishes(config.maxDishes, config.seed))),
              randManager,
              config.waitingTime),
            s"Customer$i")
        }
        customers.foreach { c => c ! Customer.Start }

        Behaviors.same

      case Stop =>
        Behaviors.stopped
    }
  }

  def generateDishes(maxItems: Int, seed: Long): Khinkali = {
    val rand = new Random(seed)

    val count = rand.nextInt

    val stuffing: Stuffing = rand.nextInt % 3 match {
      case 0 => Beef
      case 1 => Chicken
      case 2 => Vegan
    }
    Khinkali(stuffing, if (maxItems > count) maxItems else count)
  }
}