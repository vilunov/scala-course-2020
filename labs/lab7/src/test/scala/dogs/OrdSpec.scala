package dogs

object OrdIntSpec extends OrdProperties[Int]("intOrd") {
  override def instance: Ord[Int] = Ord[Int]
}

object OrdLongSpec extends OrdProperties[Long]("longOrd") {
  override def instance: Ord[Long] = Ord[Long]
}

object OrdStringSpec extends OrdProperties[String]("stringOrd") {
  override def instance: Ord[String] = Ord[String]
}
