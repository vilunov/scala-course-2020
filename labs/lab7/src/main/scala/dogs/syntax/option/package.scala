package dogs.syntax

package object option {
  implicit class SyntaxOption[T](private val inner: T) extends AnyVal {
    def some: Option[T] = Some(inner)
  }
}
