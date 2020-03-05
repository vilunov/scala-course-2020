package khinkali

import pureconfig._
import pureconfig.generic.auto._

sealed trait Range
case class IntRange(left:Int, right:Int) extends Range
case class DoubleRange(left:Double, right: Double) extends Range

case class CustomerConf(
                         eatDelayRange: DoubleRange, // sec,
                         khinkaliRange: List[IntRange]
                       )

case class WaiterConf(
                       resendTimeout: Double = 1, // sec - delay before resend when chef is busy
                       chefRespondTimeout: Double = 1, // sec
                     )

case class ChefConf(
                     cookingTimeRange: DoubleRange, // sec, because we not become younger here
                   )

case class ServiceConf(
                        customersNum: Int = 100,
                        chefsNum: Int = 1,
                        randomSeed: Int = 42,
                        customerConf: CustomerConf,
                        waiterConf: WaiterConf,
                        chefConf: ChefConf
                      )