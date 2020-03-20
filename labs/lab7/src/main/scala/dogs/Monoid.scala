package dogs

import dogs.syntax.monoid._

trait Semigroup[T] {
  def combine(left: T, right: T): T
}

object Semigroup {
  def apply[T](implicit instance: Semigroup[T]): Semigroup[T] = instance

  implicit def intSemigroup: Semigroup[Int] = (left, right) => left + right

  implicit def longSemigroup: Semigroup[Long] = (left, right) => left + right

  implicit def floatSemigroup: Semigroup[Float] = (left, right) => left + right

  implicit def doubleSemigroup: Semigroup[Double] =
    (left, right) => left + right

  implicit def listSemigroup[T]: Semigroup[List[T]] =
    (left, right) => left ::: right
}

trait Monoid[T] extends Semigroup[T] {
  def unit: T
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] =
    new Monoid[Map[K, V]] {
      override def combine(left: Map[K, V], right: Map[K, V]): Map[K, V] = {
        left ++ right
      }

      override def unit: Map[K, V] = Map()
    }

  implicit def intMonoid: Monoid[Int] = new Monoid[Int] {
    override def combine(left: Int, right: Int): Int = left + right

    override def unit: Int = 0
  }

  implicit def longMonoid: Monoid[Long] = new Monoid[Long] {
    override def combine(left: Long, right: Long): Long = left + right

    override def unit: Long = 0
  }

  implicit def floatMonoid: Monoid[Float] = new Monoid[Float] {
    override def combine(left: Float, right: Float): Float = left + right

    override def unit: Float = 0.0f
  }

  implicit def doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override def combine(left: Double, right: Double): Double = left + right

    override def unit: Double = 0.0
  }

  implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
    override def combine(left: List[T], right: List[T]): List[T] = left ++ right

    override def unit: List[T] = List[T]()
  }

  implicit def equalityMonoid[T]: Monoid[Equality[T]] =
    new Monoid[Equality[T]] {
      override def combine(left: Equality[T], right: Equality[T]): Equality[T] =
        (a, b) => left.equal(a, b) && right.equal(a, b)

      override def unit: Equality[T] = (_, _) => true
    }
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup {
  def apply[T](
    implicit instance: CommutativeSemigroup[T]
  ): CommutativeSemigroup[T] = instance

  implicit def intSemigroup: Semigroup[Int] = (left, right) => left + right

  implicit def longSemigroup: Semigroup[Long] = (left, right) => left + right

  implicit def floatSemigroup: Semigroup[Float] = (left, right) => left + right

  implicit def doubleSemigroup: Semigroup[Double] =
    (left, right) => left + right
}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {
  def apply[T](implicit instance: CommutativeMonoid[T]): CommutativeMonoid[T] =
    instance

  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]
    : CommutativeMonoid[Map[K, V]] = new CommutativeMonoid[Map[K, V]] {
    override def combine(left: Map[K, V], right: Map[K, V]): Map[K, V] = {
      val commonKeys = left.keySet & right.keySet

      def commonKeysFilter(in: Boolean): ((K, V)) => Boolean = {
        case (key: K, _: V) => if (commonKeys.contains(key)) in else !in
        case _              => false
      }

      val newMap = left.filter(commonKeysFilter(false)) ++ right.filter(
        commonKeysFilter(false)
      )
      newMap ++ left.filter(commonKeysFilter(true)).map {
        case (key: K, value: V) =>
          (key, value |+| right(key))
      }
    }

    override def unit: Map[K, V] = Map()
  }

  implicit def intMonoid: CommutativeMonoid[Int] = new CommutativeMonoid[Int] {
    override def combine(left: Int, right: Int): Int = left + right

    override def unit: Int = 0
  }

  implicit def longMonoid: CommutativeMonoid[Long] =
    new CommutativeMonoid[Long] {
      override def combine(left: Long, right: Long): Long = left + right

      override def unit: Long = 0
    }

  implicit def floatMonoid: CommutativeMonoid[Float] =
    new CommutativeMonoid[Float] {
      override def combine(left: Float, right: Float): Float = left + right

      override def unit: Float = 0.0f
    }

  implicit def doubleMonoid: CommutativeMonoid[Double] =
    new CommutativeMonoid[Double] {
      override def combine(left: Double, right: Double): Double = left + right

      override def unit: Double = 0.0
    }

  implicit def equalityMonoid[T]: CommutativeMonoid[Equality[T]] =
    new CommutativeMonoid[Equality[T]] {
      override def combine(left: Equality[T], right: Equality[T]): Equality[T] =
        (a, b) => left.equal(a, b) && right.equal(a, b)

      override def unit: Equality[T] = (_, _) => true
    }
}
