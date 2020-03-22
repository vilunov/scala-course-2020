package dogs.syntax

package object option {
  implicit class OptionWrapper[T](private val value: T) extends AnyVal {
    def some: Option[T] = Some(value)
  }
}
