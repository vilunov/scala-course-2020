package dogs

trait Semigroup[T] {
  def combine(a: T, b: T): T
}

object Semigroup {
  def apply[T](implicit semigroup: Semigroup[T]): Semigroup[T] = semigroup

  implicit val intSemigroup: Semigroup[Int] = (a, b) => ((a.toLong + b.toLong) % Int.MaxValue).toInt
  implicit val longSemigroup: Semigroup[Long] = (a, b) => a + b
  implicit val floatSemigroup: Semigroup[Float] = (a, b) => a + b
  implicit val doubleSemigroup: Semigroup[Double] = (a, b) => a + b
  implicit def listSemigroup[T]: Semigroup[List[T]] = (a, b) => a ::: b
  implicit def mapSemigroup[K, V : Semigroup]: Semigroup[Map[K, V]] = (a, b) => a.foldRight(b){
    case ((k, v), b) =>
      b.get(k).fold(b + (k -> v))(item => b + (k -> Semigroup[V].combine(v, item)))
  }
}

trait Monoid[T] extends Semigroup[T] {
  def unit: T
}

object Monoid {
  def apply[T](implicit monoid: Monoid[T]): Monoid[T] = monoid

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] = new Monoid[Map[K, V]] {
    override def unit: Map[K, V] = Map.empty[K, V]
    override def combine(a: Map[K, V], b: Map[K, V]): Map[K, V] = Semigroup.mapSemigroup[K, V].combine(a, b)
  }

  implicit def equalityMonoid[T]: Monoid[Equality[T]] = new Monoid[Equality[T]] {
    override def unit: Equality[T] = (_, _) => true
    override def combine(a: Equality[T], b: Equality[T]): Equality[T] =
      (left: T, right: T) => a.equal(left, right) && b.equal(left, right)
  }

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override val unit: Int = 0
    override def combine(a: Int, b: Int): Int = Semigroup.intSemigroup.combine(a, b)
  }

  implicit val floatMonoid: Monoid[Float] = new Monoid[Float] {
    override val unit: Float = 0.0F
    override def combine(a: Float, b: Float): Float = Semigroup.floatSemigroup.combine(a, b)
  }

  implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
    override def unit: Long = 0L
    override def combine(a: Long, b: Long): Long = Semigroup.longSemigroup.combine(a, b)
  }

  implicit val doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override def unit: Double = 0.0
    override def combine(a: Double, b: Double): Double = Semigroup.doubleSemigroup.combine(a, b)
  }

  implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
    override def unit: List[T] = List.empty[T]
    override def combine(a: List[T], b: List[T]): List[T] = Semigroup.listSemigroup.combine(a, b)
  }
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup {
  def apply[T](implicit CS: CommutativeSemigroup[T]): CommutativeSemigroup[T] = CS

  implicit val intCS: CommutativeSemigroup[Int] = (l, r) => Semigroup.intSemigroup.combine(l, r)
  implicit val floatCS: CommutativeSemigroup[Float] = (l, r) => Semigroup.floatSemigroup.combine(l, r)
  implicit val doubleCS: CommutativeSemigroup[Double] = (l, r) => Semigroup.doubleSemigroup.combine(l, r)
  implicit val longCS: CommutativeSemigroup[Long] = (l, r) => Semigroup.longSemigroup.combine(l, r)
  implicit def listCS[T]: CommutativeSemigroup[List[T]] = (a: List[T], b: List[T]) => Semigroup.listSemigroup.combine(a, b)
  implicit def mapCS[K, V : CommutativeSemigroup]: CommutativeSemigroup[Map[K, V]] = (a: Map[K, V], b: Map[K, V]) =>
    Semigroup.mapSemigroup(implicitly[CommutativeSemigroup[V]]).combine(a, b)
}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {
  def apply[T](implicit CM: CommutativeMonoid[T]): CommutativeMonoid[T] = CM

  implicit val intCM: CommutativeMonoid[Int] = new CommutativeMonoid[Int] {
    override def unit: Int = Monoid[Int].unit
    override def combine(a: Int, b: Int): Int = Monoid.intMonoid.combine(a, b)
  }

  implicit val longCM: CommutativeMonoid[Long] = new CommutativeMonoid[Long] {
    override def unit: Long = Monoid[Long].unit
    override def combine(a: Long, b: Long): Long = Monoid.longMonoid.combine(a, b)
  }

  implicit val floatCM: CommutativeMonoid[Float] = new CommutativeMonoid[Float] {
    override def unit: Float = Monoid[Float].unit
    override def combine(a: Float, b: Float): Float = Monoid.floatMonoid.combine(a, b)
  }

  implicit val doubleCM: CommutativeMonoid[Double] = new CommutativeMonoid[Double] {
    override def unit: Double = Monoid[Double].unit
    override def combine(a: Double, b: Double): Double = Monoid.doubleMonoid.combine(a, b)
  }

  implicit def listCM[T: Ord]: CommutativeMonoid[List[T]] = new CommutativeMonoid[List[T]] {
    override def unit: List[T] = Monoid[List[T]].unit
    override def combine(a: List[T], b: List[T]): List[T] = Ord[List[T]].compare(a, b) match {
        case OrdResult.Less => Monoid[List[T]].combine(b, a)
        case _ => Monoid[List[T]].combine(a, b)
    }
  }

  implicit def mapCM[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] = new CommutativeMonoid[Map[K, V]] {
    override def unit: Map[K, V] = Map.empty[K, V]
    override def combine(a: Map[K, V], b: Map[K, V]): Map[K, V] = Monoid.mapMonoid(implicitly[CommutativeSemigroup[V]]).combine(a, b)
  }
}

