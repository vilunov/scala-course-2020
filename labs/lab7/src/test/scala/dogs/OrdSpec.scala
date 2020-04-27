package dogs

//here 100 random tests are performed

object OrdIntSpec extends EqualityProperties[Int]("intOrd-Equality") {
  override def instance: Equality[Int] = Ord[Int]
}

object OrdLongSpec extends EqualityProperties[Long]("longOrd-Equality") {
  override def instance: Equality[Long] = Ord[Long]
}

object OrdStringSpec extends EqualityProperties[String]("stringOrd-Equality"){
  override def instance: Equality[String] = Ord[String]
}

object IntOrdSpec extends OrdProperties[Int]("intOrd-Compare"){
  override def instance: Ord[Int]=Ord[Int]
}

object LongOrdSpec extends OrdProperties[Long]("longOrd-Compare"){
  override def instance: Ord[Long]=Ord[Long]
}

object StringOrdSpec extends OrdProperties[String]("stringOrd-Compare"){
  override def instance: Ord[String]=Ord[String]
}
