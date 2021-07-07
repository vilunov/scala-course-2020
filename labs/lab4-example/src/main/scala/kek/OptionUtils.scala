package kek

object OptionUtils {
  def sequence[T](list: List[Option[T]]): Option[List[T]] = {
    val flatList = list.flatten
    if (flatList.length == list.length)
      Some(flatList)
    else
      None
  }
}
