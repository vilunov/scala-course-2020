package week10

import week10.syntaxFunctor._
import week10.syntaxMonad._
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext}

object Main extends App{
  implicit val ec: ExecutionContext = ExecutionContext.global

  val listAsMonad = List(1, 2, 3).flatMap((i: Int) => List(i, i))
  println(listAsMonad)

  val listFunctor = List(1, 2, 3).fmap(_.toString)
  println(listFunctor)

  val vectorAsMonad = Vector(1, 2, 3).flatMap((i: Int) => List(i, i))
  println(vectorAsMonad)

  val vectorFunctor =  Vector(1, 2, 3).fmap(_.toString)
  println(vectorFunctor)

  val eitherAsMonad = Monad.monadForEither[Int].flatMap((i: String) => Right(i.toUpperCase()), Right("mew"))
  println(eitherAsMonad)

  val eitherAsFunctor= Functor.functorForEither[Int].fmap((i: String) => Right(i.toUpperCase()), Right("mew"))
  println(eitherAsFunctor)

  val optionAsMonad = Monad.monadForOption.flatMap((i: String) => Some(i.toUpperCase()), Some("mew"))
  println(optionAsMonad)

  val optionAsFunctor = Functor.functorForOption.fmap((i: String) => Some(i.toUpperCase()), Some("mew"))
  println(optionAsFunctor)

  val idAsFunctor = Functor.functorforId.fmap((i: String) => (i.toUpperCase()), ("mew"))
  println(idAsFunctor)

  val idAsMonad = Monad.monadForId.fmap((i: String) => (i.toUpperCase()), ("mew"))
  println(idAsMonad)

  val futureAsMonad = Monad.monadForFuture.fmap((i: String) => (i.toUpperCase()), Monad.monadForFuture.pure("Future Mew"))
  val res = Await.result(futureAsMonad, 5.seconds)
  println(res)

  val futureAsFunctor = Functor.functorForFuture.fmap((i: String) => (i.toUpperCase()), Monad.monadForFuture.pure("Future Mew"))
  val r = Await.result(futureAsFunctor, 5.seconds)
  println(r)

  val f1Monad = ((i: Int) => i + 1).flatMap(b => _ +b)
  println(f1Monad(15))

// for functor only
  val map = Map(1 -> 2, 3 -> 4)
  val mapAsFunctor = Functor.functorforMap[Int].fmap((i:Int)=>((i+1).toString), map)
  println(mapAsFunctor)

  val mapAsFunctor2 = for {
    (k, v) <- map
  } yield (k, v + 0.5)
  println(mapAsFunctor2)

//  for monad only
  val wrapperMonad =Monad.monadForWraper[Int].flatMap((i: Option[Int]) =>
    Monad.monadForWraper.pure(i.map(_.toString)), Monad.monadForWraper.pure(Option(1)))
  val result = Await.result(wrapperMonad.value(1), 5.seconds)
  println(result)

  val optionSeq = Monad.sequence(List(Option(1), Option(2)))
  println(optionSeq)

}
