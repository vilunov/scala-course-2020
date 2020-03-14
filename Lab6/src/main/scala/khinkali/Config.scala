package khinkali

case class Config(
                   randomSeed: Long,
                   nCustomers: Int,
                   nChefs: Int,
                   customerOrderingMin: Double,
                   customerOrderingMax: Double,
                   customerEatingMin: Double,
                   customerEatingMax: Double,
                   beefCookingMin: Double,
                   beefCookingMax: Double,
                   muttonCookingMin: Double,
                   muttonCookingMax: Double,
                   cheeseAndMushroomsCookingMin: Double,
                   cheeseAndMushroomsCookingMax: Double
                 )
