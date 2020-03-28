package dogs

import dogs.Monad._
import dogs.syntax.monad._

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object Main extends App {
  def sequence[F[+_] : Monad, T](v: List[F[T]]): F[List[T]] = {
    v.map(x => x.map(y => List(y)))
      .fold(Monad[F].pure(List.empty[T]))((a: F[List[T]], b: F[List[T]]) =>
        a.flatMap(x => b.map(y => x ++ y)))
  }

  val listFunctor = List(1, 2, 3).fmap(_.toString + "%")
  println(listFunctor)

  val listMonad = Monad[List].flatMap((i: Int) => List(i, i), List(1, 2, 3))
  println(listMonad)

  val vectorAsMonad = Monad[Vector].flatMap((i: Int) => Vector(i, i), Vector(1, 2, 3))
  println(vectorAsMonad)

  val optionAsMonad = Monad[Option].flatMap((i: Int) => Some(i.toString + "%"), Option(1))
  println(optionAsMonad)

  val eitherAsMonad = Monad.eitherMonad[Int].flatMap((i: String) => Right(i.toUpperCase()), Right("kek"))
  println(eitherAsMonad)

  val f1Monad = ((i: Int) => i + 1).flatMap(b => _ * b)
  println(f1Monad(11))

  val int = 1: Int
  val idFor = for {
    v <- int: Id[Int]
  } yield v + 1
  println(idFor)

  val list = List(1, 2, 3)
  val listFor = for {
    v <- list
  } yield v + 1
  println(listFor)

  val mapAsFunctor = Map(1 -> 2, 3 -> 4).fmap(_ * 2)
  println(mapAsFunctor)

  val map = Map(1 -> 2, 3 -> 4)
  val mapFor = for {
    (k, v) <- map
  } yield (k, v * 2)
  println(mapFor)

  implicit val ec: ExecutionContext = ExecutionContext.global
  val wM = Monad.wrapperFutureMonad[Int]
  val wrapperMonad = wM.flatMap((i: Option[Int]) => wM.pure(i.map(_.toString + "%")), wM.pure(Option(1)))
  val result = Await.result(wrapperMonad.value(1), 5.seconds)
  println(result)

  val optionSeq = sequence(List(Option(1), Option(2), None))
  println(optionSeq)

  val optionSeqSome = sequence(List(Option(1), Option(2)))
  println(optionSeqSome)

  val eitherSeq = sequence(List(Right(1), Right(2), Left(3), Right(4), Left(5)))
  println(eitherSeq)

  val eitherSeqRight = sequence(List[Either[Int, Int]](Right(1), Right(2), Right(3)))
  println(eitherSeqRight)
}
