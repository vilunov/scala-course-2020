package khinkali

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import pureconfig.ConfigSource

import scala.concurrent.duration._
import scala.util.Random

object Customer {
  sealed trait Command

  case object Start extends Command
  case class LeaveOrder(order: CustomerOrder) extends Command
  case object Eat extends Command
  case object Leave extends Command
  val r = scala.util.Random

  def apply(waiter: ActorRef[Waiter.Command],cafe: ActorRef[Cafe.Command], cafeConfig:CafeConfig, r:Random.type ): Behavior[Command] =
    start(waiter, cafe, cafeConfig, r)

  def start(waiter: ActorRef[Waiter.Command],cafe: ActorRef[Cafe.Command], cafeConfig: CafeConfig, r: Random.type ): Behavior[Command] =
    Behaviors.receive { (ctx, msg) =>
      val maxNumOfKhinkali = cafeConfig.maxKhinkali

      msg match {
        case Start =>
          var beef   = r.nextInt(maxNumOfKhinkali+1)
          var mutton = r.nextInt(maxNumOfKhinkali+1)
          var cheese = r.nextInt(maxNumOfKhinkali+1)
          while (beef==0 && cheese==0 && mutton==0){
            beef   = r.nextInt(maxNumOfKhinkali+1)
            mutton = r.nextInt(maxNumOfKhinkali+1)
            cheese = r.nextInt(maxNumOfKhinkali+1)
          }
          val order = CustomerOrder(List(Khinkali(Stuffing.Beef, beef),
                                        Khinkali(Stuffing.Mutton, mutton),
                                        Khinkali(Stuffing.CheeseAndMushrooms, cheese)))
          val maxDecisionTime = cafeConfig.maxDecisionTime
          val minDecisionTime = cafeConfig.minDecisionTime
          val decision = r.nextInt(maxDecisionTime+1-minDecisionTime)+minDecisionTime
          ctx.scheduleOnce(decision.millisecond, ctx.self, LeaveOrder(order))
          leaveOrder(waiter, cafe, cafeConfig, r)
        case _ => Behaviors.same
      }
    }

  def leaveOrder(waiter: ActorRef[Waiter.Command], cafe: ActorRef[Cafe.Command], cafeConfig: CafeConfig, r: Random.type): Behavior[Command]
  = Behaviors.receive { (ctx, msg) =>
    msg match {
      case LeaveOrder(order) =>
        ctx.log.info(s"Leaving order $order")
        waiter ! Waiter.TakeOrder(order, ctx.self)
        waitForEat(cafe, cafeConfig, r)
      case _ => Behaviors.same
    }
  }

  def waitForEat(cafe: ActorRef[Cafe.Command], cafeConfig: CafeConfig, r: Random.type): Behavior[Command]
  = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Eat =>
        ctx.log.info(s"Now eating")
        val eating = cafeConfig.minEatingTime+r.nextInt(cafeConfig.maxEatingTime-cafeConfig.minEatingTime+1)
        ctx.scheduleOnce(eating.millisecond, ctx.self, Leave)
        waitToLeave(cafe)
      case _ => Behaviors.same
    }
  }

  def waitToLeave(cafe: ActorRef[Cafe.Command]): Behavior[Command] = Behaviors.receive { (ctx, msg) =>
    msg match {
      case Leave =>
        ctx.log.info(s"Now leaving")
        cafe !  Cafe.ClientExits
        Behaviors.stopped
      case _ => Behaviors.same
    }
  }

}
