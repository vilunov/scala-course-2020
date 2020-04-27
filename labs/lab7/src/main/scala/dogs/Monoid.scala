package dogs

trait Semigroup[T]{
  def combine(x :T, y:T):T

}

object Semigroup{
  def apply[T](implicit instance: Semigroup[T]): Semigroup[T] = instance

//todo - please, write in a review how to pass here Numeric to avoid the same lines
// the following line did not worked
// implicit def numCombine[T:Numeric]:Semigroup[T]=(left, right)=>left+right
  implicit val intCombine: Semigroup[Int]=(left, right)=>left+right
  implicit val floatCombine: Semigroup[Float]=(left, right)=>left+right
  implicit val doubleCombine: Semigroup[Double]=(left, right)=>left+right
  implicit val longCombine: Semigroup[Long]=(left, right)=>left+right
  implicit val stringCombine: Semigroup[String]=(left, right)=>left+right

  implicit def listCombine[T] : Semigroup[List[T]] =(left, right)=>left++right

  implicit def mapCombine[K,V:Semigroup]:Semigroup[Map[K, V]] =new Semigroup[Map[K, V]] {
    override def combine(left: Map[K, V], right: Map[K, V]): Map[K, V] =
     {
      val left_keys = left.keys.toSet
      val right_keys = right.keys.toSet
      val intersection = left_keys.intersect(right_keys)
      val left_rest = left_keys.diff(intersection)
      val right_rest = right_keys.diff(intersection)
      val res_map = left ++ right
      val states = intersection.map(intr => intr -> Semigroup[V].combine(left(intr), right(intr))).toMap
      val res = res_map ++ states
      res
    }
  }
}

trait Monoid[T] extends Semigroup[T]{
  val unit: T
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override val unit: Int = 0

    override def combine(x: Int, y: Int): Int = Semigroup.intCombine.combine(x,y)
  }

  implicit val doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override val unit: Double = 0.0

    override def combine(x: Double, y: Double): Double = Semigroup.doubleCombine.combine(x,y)
  }

  implicit val floatMonoid: Monoid[Float] = new Monoid[Float] {
    override val unit: Float = 0

    override def combine(x: Float, y: Float): Float = Semigroup.floatCombine.combine(x,y)
  }

  implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
    override val unit: Long = 0

    override def combine(x: Long, y: Long): Long = Semigroup.longCombine.combine(x,y)
  }

  implicit val stringMonoid : Monoid[String] = new Monoid[String] {
    override val unit: String = ""

    override def combine(x: String, y: String): String = Semigroup.stringCombine.combine(x,y)
  }

  implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
    override val unit: List[T] = List.empty

    override def combine(x: List[T], y: List[T]): List[T] = Semigroup.listCombine.combine(x, y)
  }

  implicit def mapMonoid[K,V: Semigroup] : Monoid[Map[K,V]] = new Monoid[Map[K, V]] {
    override val unit: Map[K, V] = Map.empty

    override def combine(x: Map[K, V], y: Map[K, V]): Map[K, V] = Semigroup.mapCombine[K,V].combine(x,y)
  }

  implicit def equalityMonoid[T]: Monoid[Equality[T]] = new Monoid[Equality[T]] {
    override val unit: Equality[T] = (left: T, right: T) => true

    override def combine(x: Equality[T], y: Equality[T]): Equality[T] = new Equality[T] {
      override def equal(left: T, right: T): Boolean = x.equal(left, right) && y.equal(left,right)
    }
  }
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup{

  def apply[T](implicit instance: CommutativeSemigroup[T]): CommutativeSemigroup[T] = instance

  implicit val intCombine: CommutativeSemigroup[Int]=(left, right)=>Semigroup.intCombine.combine(left, right)
  implicit val floatCombine: CommutativeSemigroup[Float]=(left, right)=>Semigroup.floatCombine.combine(left, right)
  implicit val doubleCombine: CommutativeSemigroup[Double]=(left, right)=>Semigroup.doubleCombine.combine(left, right)
  implicit val longCombine: CommutativeSemigroup[Long]=(left, right)=>Semigroup.longCombine.combine(left, right)
  implicit def listCombine[T] : CommutativeSemigroup[List[T]] = (left, right) => Semigroup.listCombine.combine(left,right)

  implicit def mapCombine[K,V:CommutativeSemigroup]:CommutativeSemigroup[Map[K, V]] =(left, right)
  => Semigroup.mapCombine[K,V].combine(left,right)

}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {
  def apply[T](implicit instance: CommutativeMonoid[T]): CommutativeMonoid[T] = instance

  implicit val intCommutativeMonoid: CommutativeMonoid[Int] = new CommutativeMonoid[Int] {
    override val unit: Int = Monoid.intMonoid.unit
    override def combine(x: Int, y: Int): Int = Monoid.intMonoid.combine(x, y)
  }

  implicit val floatCommutativeMonoid: CommutativeMonoid[Float] = new CommutativeMonoid[Float] {
    override val unit: Float = Monoid.floatMonoid.unit

    override def combine(x: Float, y: Float): Float = Monoid.floatMonoid.combine(x, y)
  }

  implicit val doubleCommutativeMonoid: CommutativeMonoid[Double] = new CommutativeMonoid[Double] {
    override val unit: Double = Monoid.doubleMonoid.unit

    override def combine(x: Double, y: Double): Double = Monoid.doubleMonoid.combine(x,y)
  }

  implicit val longCommutativeMonoid: CommutativeMonoid[Long] = new CommutativeMonoid[Long] {
    override val unit: Long = Monoid.longMonoid.unit

    override def combine(x: Long, y: Long): Long = Monoid.longMonoid.combine(x,y)
  }

  implicit def listCommutativeMonoid[T] : CommutativeMonoid[List[T]] = new CommutativeMonoid[List[T]] {
    override val unit: List[T] = Monoid.listMonoid[T].unit
    override def combine(x: List[T], y: List[T]): List[T] = Monoid.listMonoid.combine(x, y)
  }

  implicit def mapCommutativeMonoid[K, V: CommutativeMonoid]: CommutativeMonoid[Map[K, V]] = new CommutativeMonoid[Map[K, V]] {
    override val unit: Map[K, V] = Monoid.mapMonoid[K,V].unit

    override def combine(x: Map[K, V], y: Map[K, V]): Map[K, V] = Monoid.mapMonoid[K,V].combine(x,y)
  }
}


