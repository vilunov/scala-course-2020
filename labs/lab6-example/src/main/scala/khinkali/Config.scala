package khinkali

import scala.util.Random

case class Boundaries(min: Int, max: Int) {
  def randomWithin(random: Random): Int = {
    random.between(this.min, this.max)
  }
}

case class Config(
    numberOfCustomers: Int,
    numberOfChefs: Int,
    seed: Long,
    chefConfig: ChefConfig,
    customerConfig: CustomerConfig
)

case class CustomerConfig(
    orderDecisionTimeBoundaries: Boundaries,
    eatingTimeBoundaries: Boundaries,
    orderSizeBoundaries: Boundaries,
    khinkaliAmountBoundaries: Boundaries
)

case class ChefConfig(cookingTimeBoundaries: Boundaries)
