package lab7.syntax

package object option {

  implicit class SyntaxBetterOption[T](val value: T) extends AnyVal {
    def some: Option[value.type] = Some(value)
  }

}
