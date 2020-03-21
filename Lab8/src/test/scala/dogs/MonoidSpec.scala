package dogs

object SemigroupIntSpec extends SemigroupProperties[Int]("intSemigroup") {
  override implicit val instance: Semigroup[Int] = Monoid.intMonoid
}

object SemigroupFloatSpec extends SemigroupProperties[Float]("floatSemigroup") {
  override implicit val instance: Semigroup[Float] = Monoid.floatMonoid
}

object MonoidIntSpec extends MonoidProperties[Int]("intMonoid") {
  override implicit val instance: Monoid[Int] = Monoid.intMonoid
}

object MonoidFloatSpec extends MonoidProperties[Float]("floatMonoid") {
  override implicit val instance: Monoid[Float] = Monoid.floatMonoid
}

object CommutativeSemigroupIntSpec extends CommutativeSemigroupProperties[Int]("intCommutativeSemigroup") {
  override implicit val instance: CommutativeSemigroup[Int] = CommutativeMonoid.intCommutativeMonoid
}

object CommutativeSemigroupFloatSpec extends CommutativeSemigroupProperties[Float]("floatCommutativeSemigroup") {
  override implicit val instance: CommutativeSemigroup[Float] = CommutativeMonoid.floatCommutativeMonoid
}

object CommutativeMonoidIntSpec extends CommutativeMonoidProperties[Int]("intCommutativeMonoid") {
  override implicit val instance: CommutativeMonoid[Int] = CommutativeMonoid.intCommutativeMonoid
}

object CommutativeMonoidFloatSpec extends CommutativeMonoidProperties[Float]("floatCommutativeMonoid") {
  override implicit val instance: CommutativeMonoid[Float] = CommutativeMonoid.floatCommutativeMonoid
}