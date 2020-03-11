package khinkali

import com.typesafe.config.ConfigFactory

import scala.util.Random

case class CustomerConfig(N: Int,
                          order: (Double, Double),
                          eat: (Double, Double))

case class ChefConfig(N: Int,
                      beef: (Double, Double),
                      mutton: (Double, Double),
                      cheese: (Double, Double))

object Config {
  private val cfg = ConfigFactory.load("application")
  val rng: Random = new Random(cfg.getLong("seed"))
  val chefConfig: ChefConfig = ChefConfig(
    cfg.getInt("chefs.N"),
    (cfg.getDouble("chefs.beef.from"), cfg.getDouble("chefs.beef.to")),
    (cfg.getDouble("chefs.mutton.from"), cfg.getDouble("chefs.mutton.to")),
    (cfg.getDouble("chefs.cheese.from"), cfg.getDouble("chefs.cheese.to"))
  )
  val customerConfig: CustomerConfig = CustomerConfig(
    cfg.getInt("customers.N"),
    (
      cfg.getDouble("customers.order.from"),
      cfg.getDouble("customers.order.to")
    ),
    (cfg.getDouble("customers.eat.from"), cfg.getDouble("customers.eat.to"))
  )
}
