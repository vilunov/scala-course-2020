package dogs.syntax

package object either {

  implicit class SyntaxEither[T](val inner: T) extends AnyVal {
    def left: Either[T, Nothing] = Left(inner)

    def right: Either[Nothing, T] = Right(inner)
  }

}
