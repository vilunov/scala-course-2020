package dogs.syntax

package object either {

  implicit class left_o_righter[T](private val obj: T) extends AnyVal {
    def left: Either[T, Nothing] = Left(obj)

    def right: Either[Nothing, T] = Right(obj)
  }

}
