package dogs.syntax

package object option {

  implicit class Sommer[T](val obj: T) extends AnyVal {
    def some: Option[T] = {
      if (obj == None)
        None
      else
        Some(obj)
    }
  }

}
