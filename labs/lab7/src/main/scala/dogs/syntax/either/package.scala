package dogs.syntax

package object either {
  implicit class SyntaxEither[T](private val inner: T) extends AnyVal {
    def right: Either[Nothing, T] = Right(inner)
    def left: Either[T, Nothing] = Left(inner)
  }
}
