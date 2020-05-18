package khinkali

import pureconfig._
import pureconfig.generic.auto._

case class CafeConfig (
    numOfChefs      :Int,
    numOfCustomers  :Int,
    maxCookingTime  : Int,
    minCookingTime  : Int,
    maxKhinkali     : Int,
    maxDecisionTime : Int,
    minDecisionTime : Int,
    maxEatingTime   : Int,
    minEatingTime   : Int
)
