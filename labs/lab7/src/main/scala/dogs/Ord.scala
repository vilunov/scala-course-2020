package dogs

sealed abstract class OrdResult {
  def inverse: OrdResult
}

object OrdResult {

  case object Less extends OrdResult {
    override val inverse: OrdResult = Greater
  }

  case object Equal extends OrdResult {
    override val inverse: OrdResult = Equal
  }

  case object Greater extends OrdResult {
    override val inverse: OrdResult = Less
  }

  private[dogs] def fromInt(res: Int): OrdResult = res match {
    case i if i < 0 => OrdResult.Less
    case 0          => OrdResult.Equal
    case _          => OrdResult.Greater
  }

}

trait PartialEquality[-T] {
  def equal(left: T, right: T): Boolean
}

object PartialEquality {
  def apply[T](implicit instance: PartialEquality[T]): PartialEquality[T] =
    instance

  implicit val doublePartialEquality: PartialEquality[Double] =
    PartialOrd.doublePartialOrd
  implicit val floatPartialEquality: PartialEquality[Float] =
    PartialOrd.floatPartialOrd
}

trait Equality[-T] extends PartialEquality[T]

object Equality {
  def apply[T](implicit instance: Equality[T]): Equality[T] = instance
}

trait PartialOrd[-T] extends PartialEquality[T] {
  def partialCompare(left: T, right: T): Option[OrdResult]

  override def equal(left: T, right: T): Boolean =
    partialCompare(left, right).fold(false)(_ == OrdResult.Equal)
}

object PartialOrd {
  def apply[T](implicit instance: PartialOrd[T]): PartialOrd[T] = instance

  implicit val doublePartialOrd: PartialOrd[Double] = (left, right) =>
    if (left.isNaN || right.isNaN) None
    else Some(OrdResult.fromInt(left.compareTo(right)))
  implicit val floatPartialOrd: PartialOrd[Float] = (left, right) =>
    if (left.isNaN || right.isNaN) None
    else Some(OrdResult.fromInt(left.compareTo(right)))
}

trait Ord[-T] extends PartialOrd[T] with Equality[T] {
  final override def partialCompare(left: T, right: T): Some[OrdResult] =
    Some(compare(left, right))

  override def equal(left: T, right: T): Boolean =
    compare(left, right) == OrdResult.Equal

  def compare(left: T, right: T): OrdResult
}

object Ord {
  def apply[T](implicit instance: Ord[T]): Ord[T] = instance

  implicit val intOrd: Ord[Int] = (left, right) =>
    OrdResult.fromInt(left.compareTo(right))
  implicit val longOrd: Ord[Long] = (left, right) =>
    OrdResult.fromInt(left.compareTo(right))
  implicit val stringOrd: Ord[String] = (left, right) =>
    OrdResult.fromInt(left.compareTo(right))
}
