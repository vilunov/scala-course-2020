package dogs

object Main extends App {
  val map = Map(1 -> 1)
  println(map(1))
  val map2: Map[Int, Int] = map.map { case (key, value) =>
    println("HI")
    key -> value * 2
  }
  println(map2(1))
  println(map2(1))
  println(map2(1))
  println(map2(1))
  println(map2(1))

}
