package khinkali

import scala.util.Random

case class Config(
   numOfCustomers: Int,
   numOfChefs: Int,
   seed: Int,
   customerConf: CustomerConf)

case class MyRange(left: Int, right: Int) {
  def inRange(rand: Random): Int = rand.between(left, right)
}

case class CustomerConf(
   numOfDishesRange: MyRange,
   numOfKhinkalisRange: MyRange,
   decisionTimeRange: MyRange,
   eatingTimeRange: MyRange)