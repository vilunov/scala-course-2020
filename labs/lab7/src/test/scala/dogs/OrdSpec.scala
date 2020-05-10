package dogs

object OrdIntSpec extends EqualityProperties[Int]("intOrd") {
  override def instance: Equality[Int] = Ord[Int]
}

object OrdLongSpec extends EqualityProperties[Long]("longOrd") {
  override def instance: Equality[Long] = Ord[Long]
}

object OrdStringSpec extends EqualityProperties[String]("stringOrd") {
  override def instance: Equality[String] = Ord[String]
}

//object OrdListSpec extends EqualityProperties[List[Int]](name = "listIntOrd") {
//  override def instance: Equality[List[Int]] = Ord[List[Int]]
//}
