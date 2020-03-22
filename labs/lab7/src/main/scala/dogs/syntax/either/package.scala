package dogs.syntax

package object either {
  implicit class EitherWrapper[T](val value: T) extends AnyVal {
    def left: Either[T, Nothing] = Left(value)
    def right: Either[Nothing, T] = Right(value)
  }
}
