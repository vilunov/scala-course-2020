package dogs

trait Semigroup[T] {
  def combine(a: T, b: T): T
}

object Semigroup {
  def apply[T](implicit instance: Semigroup[T]): Semigroup[T] = instance

  implicit val intSemigroup: Semigroup[Int] = (left, right) => left + right
  implicit val longSemigroup: Semigroup[Long] = (left, right) => left + right
  implicit val floatSemigroup: Semigroup[Float] = (left, right) => left + right
  implicit val doubleSemigroup: Semigroup[Double] = (left, right) => left + right
  implicit def listSemigroup[T]: Semigroup[List[T]] = (left, right) => left ++ right
  implicit def mapSemigroup[K, V: Semigroup]: Semigroup[Map[K, V]] = (left, right) => {
    val updates = for {
      (bKey, bVal) <- left
      (sKey, sVal) <- right if bKey == sKey
    } yield {
      (bKey, Semigroup[V].combine(bVal, sVal))
    }
    left ++ right ++ updates
  }
}

trait Monoid[T] extends Semigroup[T] {
  def unit: T
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def unit: Int = 0
    override def combine(a: Int, b: Int): Int = a + b
  }

  implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
    override def unit: Long = 0
    override def combine(a: Long, b: Long): Long = a + b
  }

  implicit val floatMonoid: Monoid[Float] = new Monoid[Float] {
    override def unit: Float = 0.0f
    override def combine(a: Float, b: Float): Float = a + b
  }

  implicit val doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override def unit: Double = 0.0
    override def combine(a: Double, b: Double): Double = a + b
  }

  implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
    override def unit: List[T] = List.empty
    override def combine(a: List[T], b: List[T]): List[T] = a ++ b
  }

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] = new Monoid[Map[K, V]] {
    override def unit: Map[K, V] = Map.empty
    override def combine(a: Map[K, V], b: Map[K, V]): Map[K, V] = Semigroup.mapSemigroup[K, V].combine(a, b)
  }

  implicit def equalityMonoid[T]: Monoid[Equality[T]] = ???
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup {
  def apply[T](implicit instance: CommutativeSemigroup[T]): CommutativeSemigroup[T] = instance

  implicit val intCSemigroup: CommutativeSemigroup[Int] = (left, right) => left + right
  implicit val longCSemigroup: CommutativeSemigroup[Long] = (left, right) => left + right
  implicit val floatCSemigroup: CommutativeSemigroup[Float] = (left, right) => left + right
  implicit val doubleCSemigroup: CommutativeSemigroup[Double] = (left, right) => left + right
  implicit def mapCSemigroup[K, V: CommutativeSemigroup]: CommutativeSemigroup[Map[K, V]] = (left, right) => {
    val (small, big) = if (left.size < right.size) (left, right) else (right, left)
    val updates = for {
      (bKey, bVal) <- big
      (sKey, sVal) <- small if bKey == sKey
    } yield {
      (bKey, CommutativeSemigroup[V].combine(bVal, sVal))
    }
    big ++ small ++ updates
  }
}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {
  def apply[T](implicit instance: CommutativeMonoid[T]): CommutativeMonoid[T] = instance

  implicit val intCommutativeMonoid: CommutativeMonoid[Int] = new CommutativeMonoid[Int] {
    override def unit: Int = 0
    override def combine(a: Int, b: Int): Int = a + b
  }

  implicit val longCommutativeMonoid: CommutativeMonoid[Long] = new CommutativeMonoid[Long] {
    override def unit: Long = 0
    override def combine(a: Long, b: Long): Long = a + b
  }

  implicit val floatCommutativeSemigroup: CommutativeMonoid[Float] = new CommutativeMonoid[Float] {
    override def unit: Float = 0.0f
    override def combine(a: Float, b: Float): Float = a + b
  }

  implicit val doubleCommutativeSemigroup: CommutativeMonoid[Double] = new CommutativeMonoid[Double] {
    override def unit: Double = 0.0
    override def combine(a: Double, b: Double): Double = a + b
  }

  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] =
    new CommutativeMonoid[Map[K, V]] {
      override def unit: Map[K, V] = Map.empty
      override def combine(a: Map[K, V], b: Map[K, V]): Map[K, V] =
        CommutativeSemigroup.mapCSemigroup[K, V].combine(a, b)
    }
}

