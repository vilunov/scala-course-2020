package dogs

object EqualityIntSpec extends EqualityProperties[Int]("intEquality") {
  override def instance: Equality[Int] = Ord[Int]
}

object EqualityLongSpec extends EqualityProperties[Long]("longEquality") {
  override def instance: Equality[Long] = Ord[Long]
}

object OrdIntSpec extends OrdProperties[Int]("intOrd") {
  override def instance: Ord[Int] = Ord[Int]
}

object OrdLongSpec extends OrdProperties[Long]("longOrd") {
  override def instance: Ord[Long] = Ord[Long]
}

object EqualityStringSpec extends EqualityProperties[String]("stringEquality") {
  override def instance: Equality[String] = Ord[String]
}