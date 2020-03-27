import lab8.syntax.monad._
import scala.collection.mutable.ArrayBuffer


package object lab8 {
  type Id[+T] = T

  def sequence[F[+ _] : Monad, T](v: List[F[T]]): F[List[T]] = {
    val unit = Monad[F].pure(List.empty[T])
    // Die hard. In red book they used one more trait (Traversible) for Monad but it too late
    val collector = ArrayBuffer[T]()
    v.foreach((i: F[T]) => i.map(t => {
      collector.append(t)
      unit
    }))
    Monad[F].pure(collector.toList)
  }
}
