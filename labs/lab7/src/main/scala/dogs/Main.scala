package dogs

import dogs.syntax.monoid._
import dogs.syntax.option._
import dogs.syntax.either._

object Main extends App {
  println(List(List(1), List(2), List(3)).foldRightSemigroup(List.empty[Int]))
  val res = Map(0 -> List(15), 2 -> List(5)) |+| Map(1 -> List(5), 2 -> List(20), 4 -> List(50))
  println(res)

  val instance = Monoid.mapMonoid[Int, List[Int]]
  val res2 = instance.combine(Map(0 -> List(15), 2 -> List(5)), instance.unit)
  println(res2)
  println(List(1, 2, 3).some)
  println("hello, world".some)
  println(List(1, 2, 3).left)
  println(List(1, 2, 3).right)
  val map1 = Map(1 -> 15, 2 -> 20) |+| Map(0 -> 10)
  val map2 = Map(0 -> 10) |+| Map(1 -> 15, 2 -> 20)

  println(Semigroup[List[Int]].combine(List(1, 2, 3), List(4, 5)))
  println(List(1, 2, 3) |+| List(4, 5))

  println(Monoid[Int].combine(5, Monoid[Int].unit))
  println(Monoid[List[Int]].combine(List(1, 2, 3), Monoid[List[Int]].unit))
  println(List.empty[Int].reduceMonoid)

  println(List(1, 2, 3, 4, 5).foldLeftSemigroup(0))
}




