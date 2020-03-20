package lab7.syntax

package object either {

  implicit class SyntaxBetterEither[T](val value: T) extends AnyVal {
    def left: Either[T, Nothing] = Left(value)

    def right: Either[Nothing, T] = Right(value)
  }

}