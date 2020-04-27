package dogs

object SemigroupInt extends SemigroupProperties[Int]("SemigroupInt"){
  override def instance :Semigroup[Int]=Semigroup[Int]
}

object SemigroupDouble extends SemigroupProperties[Double]("SemigroupDouble"){
  override def instance :Semigroup[Double]=Semigroup[Double]
}

object SemigroupLong extends SemigroupProperties[Long]("SemigroupLong"){
  override def instance :Semigroup[Long]=Semigroup[Long]
}

object SemigroupFloat extends SemigroupProperties[Float]("SemigroupFloat"){
  override def instance :Semigroup[Float]=Semigroup[Float]
}

object SemigroupString extends SemigroupProperties[String]("SemigroupString"){
  override def instance :Semigroup[String]=Semigroup[String]
}

object MonoidInt extends MonoidProperties[Int]("MonoidInt"){
  override def instance :Monoid[Int]=Monoid[Int]
}

object MonoidDouble extends MonoidProperties[Double]("MonoidDouble"){
  override def instance :Monoid[Double]=Monoid[Double]
}

object MonoidLong extends MonoidProperties[Long]("MonoidLong"){
  override def instance :Monoid[Long]=Monoid[Long]
}

object MonoidFloat extends MonoidProperties[Float]("MonoidFloat"){
  override def instance :Monoid[Float]=Monoid[Float]
}

object MonoidString extends MonoidProperties[String]("MonoidString"){
  override def instance :Monoid[String]=Monoid[String]
}

object CommutativeSemigroupInt extends CommutativeSemigroupProperties[Int]("CommutativeSemigroupInt"){
  override def instance :CommutativeSemigroup[Int]=CommutativeSemigroup[Int]
}

object CommutativeSemigroupDouble extends CommutativeSemigroupProperties[Double]("CommutativeSemigroupDouble"){
  override def instance :CommutativeSemigroup[Double]=CommutativeSemigroup[Double]
}

object CommutativeSemigroupLong extends CommutativeSemigroupProperties[Long]("CommutativeSemigroupLong"){
  override def instance :CommutativeSemigroup[Long]=CommutativeSemigroup[Long]
}

object CommutativeSemigroupFloat extends CommutativeSemigroupProperties[Float]("CommutativeSemigroupFloat"){
  override def instance :CommutativeSemigroup[Float]=CommutativeSemigroup[Float]
}
