package dogs.syntax

package object option {

  implicit class MyOption[T](val value: T) extends AnyVal {
    def some: Option[T] = Some(value)
  }
}
