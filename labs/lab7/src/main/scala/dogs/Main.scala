package dogs

import dogs.syntax.either._

object Main {
  val kek: Either[Int, Nothing] = Left(1)
  val a: Option[Nothing] = None

  val pep: Either[Nothing, Int] = 10.right
}
