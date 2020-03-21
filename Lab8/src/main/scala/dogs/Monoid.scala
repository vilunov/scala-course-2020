package dogs

trait Semigroup[T] {
  def combine(left: T, right: T): T
}

object Semigroup {

  def apply[T](implicit instance: Semigroup[T]): Semigroup[T] = instance

  implicit def semigroupFromMonoid[T](implicit monoid: Monoid[T]): Semigroup[T] = monoid
}

trait Monoid[T] extends Semigroup[T] {
  def unit: T
}

object Monoid {

  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def unit: Int = 0

    override def combine(left: Int, right: Int): Int = left + right
  }

  implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
    override def unit: Long = 0

    override def combine(left: Long, right: Long): Long = left + right
  }

  implicit val floatMonoid: Monoid[Float] = new Monoid[Float] {
    override def unit: Float = 0

    override def combine(left: Float, right: Float): Float = left + right
  }

  implicit val doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override def unit: Double = 0

    override def combine(left: Double, right: Double): Double = left + right
  }

  implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
    override def unit: List[T] = List.empty

    override def combine(left: List[T], right: List[T]): List[T] = left ++ right
  }

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] = new Monoid[Map[K, V]] {
    override def unit: Map[K, V] = Map.empty

    override def combine(left: Map[K, V], right: Map[K, V]): Map[K, V] = {
      val combined = for {
        (leftK, leftV) <- left
        (rightK, rightV) <- right if leftK == rightK
      }
        yield (leftK, Semigroup[V].combine(leftV, rightV))
      left ++ right ++ combined
    }
  }

  implicit def equalityMonoid[T]: Monoid[Equality[T]] = new Monoid[Equality[T]] {
    override def unit: Equality[T] = (_, _) => true

    override def combine(left: Equality[T], right: Equality[T]): Equality[T] =
      (a, b) => left.equal(a, b) && right.equal(a, b)
  }
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup {

  def apply[T](implicit instance: CommutativeSemigroup[T]): CommutativeSemigroup[T] = instance

  implicit def commutativeSemigroupFromCommutativeMonoid[T](implicit monoid: CommutativeMonoid[T]): CommutativeSemigroup[T] = monoid
}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {

  def apply[T](implicit instance: CommutativeMonoid[T]): CommutativeMonoid[T] = instance

  implicit val intCommutativeMonoid: CommutativeMonoid[Int] = new CommutativeMonoid[Int] {
    override def unit: Int = 0

    override def combine(left: Int, right: Int): Int = left + right
  }

  implicit val longCommutativeMonoid: CommutativeMonoid[Long] = new CommutativeMonoid[Long] {
    override def unit: Long = 0

    override def combine(left: Long, right: Long): Long = left + right
  }

  implicit val floatCommutativeMonoid: CommutativeMonoid[Float] = new CommutativeMonoid[Float] {
    override def unit: Float = 0

    override def combine(left: Float, right: Float): Float = left + right
  }

  implicit val doubleCommutativeMonoid: CommutativeMonoid[Double] = new CommutativeMonoid[Double] {
    override def unit: Double = 0

    override def combine(left: Double, right: Double): Double = left + right
  }

  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] =
    new CommutativeMonoid[Map[K, V]] {
      override def unit: Map[K, V] = Map.empty

      override def combine(left: Map[K, V], right: Map[K, V]): Map[K, V] = {
        val combined = for {
          (leftK, leftV) <- left
          (rightK, rightV) <- right if leftK == rightK
        }
          yield (leftK, CommutativeSemigroup[V].combine(leftV, rightV))
        left ++ right ++ combined
      }
    }
}