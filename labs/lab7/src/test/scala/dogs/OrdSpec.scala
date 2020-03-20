package dogs

object EqIntSpec extends EqualityProperties[Int]("intEq") {
  override def instance: Equality[Int] = Ord[Int]
}

object EqLongSpec extends EqualityProperties[Long]("longEq") {
  override def instance: Equality[Long] = Ord[Long]
}

object EqStringSpec extends EqualityProperties[String]("stringEq") {
  override def instance: Equality[String] = Ord[String]
}

object OrdIntSpec extends OrdProperties[Int]("intOrd") {
  override def instance: Ord[Int] = Ord[Int]
}

object OrdLongSpec extends OrdProperties[Long]("longOrd") {
  override def instance: Ord[Long] = Ord[Long]
}

object OrdStringSpec extends OrdProperties[String]("stringOrd") {
  override def instance: Ord[String] = Ord[String]
}