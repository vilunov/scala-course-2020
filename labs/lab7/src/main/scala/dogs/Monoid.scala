package dogs

trait Semigroup[T] {
  def combine(left: T, right: T): T
}

object Semigroup {
  // summoner
  def apply[T](implicit semigroup: Semigroup[T]): Semigroup[T] = semigroup

  // type instances
  implicit val intSemigroup: Semigroup[Int] = (left, right) => left + right
  implicit val longSemigroup: Semigroup[Long] = (left, right) => left + right
  implicit val floatSemigroup: Semigroup[Float] = (left, right) => left + right
  implicit val doubleSemigroup: Semigroup[Double] = (left, right) => left + right
  implicit def listSemigroup[T]: Semigroup[List[T]] = (left, right) => left ::: right
  implicit def mapSemigroup[K, V: Semigroup]: Semigroup[Map[K, V]] = (left, right) => {
    ???
  }
}

trait Monoid[T] extends Semigroup[T] {
  def unit: T
}

object Monoid {
  // summoner
  def apply[T](implicit monoid: Monoid[T]): Monoid[T] = monoid

  // type instances
  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def combine(left: Int, right: Int): Int = left + right
    override def unit: Int = 0
  }

  implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
    override def combine(left: Long, right: Long): Long = left + right
    override def unit: Long = 0
  }

  implicit val floatMonoid: Monoid[Float] = new Monoid[Float] {
    override def combine(left: Float, right: Float): Float = left + right
    override def unit: Float = 0
  }

  implicit val doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override def combine(left: Double, right: Double): Double = left + right
    override def unit: Double = 0
  }

  implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
    override def combine(left: List[T], right: List[T]): List[T] = left ::: right
    override def unit: List[T] = Nil
  }

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] = ???
  implicit def equalityMonoid[T]: Monoid[Equality[T]] = ???
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup {
  // summoner
  def apply[T](implicit commutativeSemigroup: CommutativeSemigroup[T]): Semigroup[T] = commutativeSemigroup

  // type instances
  implicit val intComSemigroup: CommutativeSemigroup[Int] = (left, right) => left + right
  implicit val longComSemigroup: CommutativeSemigroup[Long] = (left, right) => left + right
  implicit val floatComSemigroup: CommutativeSemigroup[Float] = (left, right) => left + right
  implicit val doubleComSemigroup: CommutativeSemigroup[Double] = (left, right) => left + right
}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {
  // summoner
  def apply[T](implicit commutativeMonoid: CommutativeMonoid[T]): CommutativeMonoid[T] = commutativeMonoid

  // type instances
  implicit val intComMonoid: CommutativeMonoid[Int] = new CommutativeMonoid[Int] {
    override def combine(left: Int, right: Int): Int = left + right
    override def unit: Int = 0
  }

  implicit val longComMonoid: CommutativeMonoid[Long] = new CommutativeMonoid[Long] {
    override def combine(left: Long, right: Long): Long = left + right
    override def unit: Long = 0
  }

  implicit val floatComMonoid: CommutativeMonoid[Float] = new CommutativeMonoid[Float] {
    override def combine(left: Float, right: Float): Float = left + right
    override def unit: Float = 0
  }

  implicit val doubleComMonoid: CommutativeMonoid[Double] = new CommutativeMonoid[Double] {
    override def combine(left: Double, right: Double): Double = left + right
    override def unit: Double = 0
  }

  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] = new CommutativeMonoid[Map[K, V]] {
    override def unit: Map[K, V] = Map.empty

    override def combine(left: Map[K, V], right: Map[K, V]): Map[K, V] = ???
  }
}

