package lab8

import lab8.syntax.monad._
import lab8.syntax.functor._
import lab8.Monad._

import scala.collection.immutable.Map

object Main extends App {
  val listAsMonad = List(1, 2, 3).flatMap((i: Int) => List(i, i))
  println(listAsMonad)

  val vectorAsMonad = Vector(1, 2, 3).flatMap((i: Int) => Vector(i, i))
  println(vectorAsMonad)

  val optionAsMonad = Option(1).flatMap((i: Int) => Some(i * 10))
  println(optionAsMonad)

  val eitherAsMonad = Right("kek"): ErrorOrT[String]
  eitherAsMonad.flatMap(i => Right(i.toUpperCase()))
  println(eitherAsMonad)

  val listAsFunctor = List(1, 2, 3).fmapFunctor(i => i % 2 == 0)
  println(listAsFunctor)

  val aboutMoiKostili = ((i: Int) => i - 10).flatMap(a => b => (a + b).toString + "!!")
  // (arg - 10) * (arg) -> str + "!!"
  println(aboutMoiKostili(20)) // 200!! - this works as shit

  val x = 1337: Int
  val identityInForComprehension = for {
    v <- x: Id[Int]
  } yield v + 1000
  println(identityInForComprehension)

  val mapAsFunctor = Map(1 -> 2, 3 -> 4).fmapFunctor(a => a + 0.5)
  println(mapAsFunctor)
  val mapAsFunctor2 = for {
    (k, v) <- Map(1 -> 2, 3 -> 4)
  } yield (k, v + 0.5)

  println(mapAsFunctor2)

  val listOfOptionAsSequence = sequence(List(Option(1), Option(2), None, Option(1337)))
  println(listOfOptionAsSequence)
}
