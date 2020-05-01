package khinkali

case class Config (
  randomSeed: Int,
  nCustomers: Int,
  nChefs: Int,

  minCookingTime: Int,
  maxCookingTime: Int,

  minSelectingTime: Int,
  maxSelectingTime: Int,

  minEatingTime: Int,
  maxEatingTime: Int,

  minDishes: Int,
  maxDishes: Int,

  minKhinkalis: Int,
  maxKhinkalis: Int
)
