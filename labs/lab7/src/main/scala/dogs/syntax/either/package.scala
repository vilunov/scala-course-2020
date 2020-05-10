package dogs.syntax

package object either {
  implicit class EitherWrapper[T](private val value: T) extends AnyVal {
    def right: Either[Nothing, T] = Right(value)
    def left: Either[T, Nothing] = Left(value)
  }

}
