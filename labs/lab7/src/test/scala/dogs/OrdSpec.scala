package dogs

object OrdIntSpec extends EqualityProperties[Int]("intOrd") {
  override def instance: Equality[Int] = Ord[Int]
}

object OrdLongSpec extends EqualityProperties[Long]("longOrd") {
  override def instance: Equality[Long] = Ord[Long]
}
