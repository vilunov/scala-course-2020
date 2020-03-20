package lab7


object IntSemigroupSpec extends SemigroupProperties[Int]("Int Semigroup spec")

object DoubleSemigroupSpec extends SemigroupProperties[Double]("Double Semigroup spec")

object FloatSemigroupSpec extends SemigroupProperties[Float]("Float Semigroup spec")

object LongSemigroupSpec extends SemigroupProperties[Long]("Long Semigroup spec")

// No arbitrary for List[Long]
//object LongListSemigroupSpec extends SemigroupProperties[List[Long]]("List Semigroup spec")


object IntCommutativeSemigroupSpec extends CommutativeSemigroupProperties[Int]("Int CommutativeSemigroup spec")

object DoubleCommutativeSemigroupSpec extends CommutativeSemigroupProperties[Double]("Double CommutativeSemigroup spec")

object FloatCommutativeSemigroupSpec extends CommutativeSemigroupProperties[Float]("Float CommutativeSemigroup spec")

object LongCommutativeSemigroupSpec extends CommutativeSemigroupProperties[Long]("Long CommutativeSemigroup spec")


object IntMonoidSpec extends MonoidProperties[Int]("Int monoid spec")

object LongMonoidSpec extends MonoidProperties[Long]("Long monoid spec")

object FloatMonoidSpec extends MonoidProperties[Float]("Float monoid spec")

object DoubleMonoidSpec extends MonoidProperties[Double]("Double monoid spec")
