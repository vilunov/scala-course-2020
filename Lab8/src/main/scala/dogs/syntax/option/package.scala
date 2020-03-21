package dogs.syntax

package object option {

  implicit class SyntaxOption[T](val inner: T) extends AnyVal {
    def some: Option[T] = Some(inner)
  }

}
