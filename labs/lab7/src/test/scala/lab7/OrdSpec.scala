package lab7

object OrdIntEqualitySpec extends EqualityProperties[Int]("intOrd-Equality") {
  override def instance: Equality[Int] = Ord[Int]
}

object OrdIntSpec extends OrdProperties[Int]("intOrd-Ordering") {
  override def instance: Ord[Int] = Ord[Int]
}


object OrdLongEqualitySpec extends EqualityProperties[Long]("longOrd-Equality") {
  override def instance: Equality[Long] = Ord[Long]
}

object OrdLongSpec extends OrdProperties[Long]("longOrd-Ordering") {
  override def instance: Ord[Long] = Ord[Long]
}


object OrdStringEqualitySpec extends EqualityProperties[String]("StringOrd-Equality") {
  override def instance: Equality[String] = Ord[String]
}

object OrdStringSpec extends OrdProperties[String]("StringOrd-Ordering") {
  override def instance: Ord[String] = Ord[String]
}
