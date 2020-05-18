package dogs

import dogs.syntax.either._
import dogs.syntax.monoid._


object Main extends App {
  // either
  // println(List(1, 2, 3).left)
  println(("Why am I doing this?").right)

  // monoid
  val monoid1 = Monoid.doubleMonoid
  monoid1.combine(1.1, 2.2)

  val monoid2 = Monoid.listMonoid[String]
  monoid2.combine(List("life", "is"), List("miserable"))

  // чота не вьезжаю как юзать эти пэкадж обджекты:(
  // List("life", "is", "miserable").reduceMonoid

  // option
  val a: Option[Nothing] = None
  val b: Option[Int] = Some(123)

}
