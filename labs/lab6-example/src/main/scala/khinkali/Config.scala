package khinkali

import pureconfig._
import pureconfig.generic.auto._

case class Range(from: Double, to: Double)

case class CustomerConfig(n: Int, order: Range, eat: Range)

case class ChefConfig(n: Int, beef: Range, mutton: Range, cheese: Range)

case class Config(customers: CustomerConfig, chefs: ChefConfig, seed: Long)
/*private val cfg = ConfigFactory.load("application")
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
  )*/
