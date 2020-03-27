package cats

import cats.syntax.monad._

import scala.collection.mutable.ArrayBuffer

object Combinator {

  def sequence[F[+_] : Monad, T](v: List[F[T]]): F[List[T]] = {
    val collector = ArrayBuffer[T]()
    v.foreach(_.map(collector.append))
    Monad[F].pure(collector.toList)
  }
}
