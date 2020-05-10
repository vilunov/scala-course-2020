package dogs

import dogs.syntax.either._
import dogs.syntax.option._

object Main {
  val kek: Either[Int, Nothing] = Left(1)
  val a: Option[Nothing] = None

  val lublu_spat_po_nocham: Either[String, Nothing] = "но нет, пишу скалу".left
  val takzche_hinkali: Either[Nothing, String] = "увы, короноавватиоывлртп".right

  case class Gold(val quantity: Int = 1337)
  val somegold: Option[Gold] = Gold().some
}
