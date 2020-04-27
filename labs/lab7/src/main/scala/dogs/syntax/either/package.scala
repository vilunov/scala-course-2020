package dogs.syntax

package object either {

  implicit class MyEither[T](private val value: T) extends AnyVal {

    def right: Either[Nothing, T] = Right(value)

    def left: Either[T, Nothing] = Left(value)

  }

}
