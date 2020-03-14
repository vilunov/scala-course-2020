package stonks

object OptionUtils {
  def sequence[T](list: List[Option[T]]): Option[List[T]] = {
    if (list.exists(_.isEmpty))
      None
    else
      Some(list.collect { case Some(x) => x })
  }
}
