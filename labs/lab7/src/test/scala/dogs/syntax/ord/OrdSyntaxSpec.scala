package dogs.syntax.ord

import dogs._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OrdSyntaxSpec extends AnyFlatSpec with Matchers {

  import OrdSyntaxSpec._

  "is" should "work for wrapped ints" in {
    Wrap(1) is Wrap(2) shouldBe false
    Wrap(1) is Wrap(1) shouldBe true
  }

  it should "work for floats" in {
    1.0f is 1.0f shouldBe true
    Float.NaN is Float.NaN shouldBe false
  }

  it should "work for wrapped floats" in {
    Wrap(1.0f) is Wrap(1.0F) shouldBe true
    Wrap(Float.NaN) is Wrap(Float.NaN) shouldBe false
  }

  "> and >=" should "work for wrapped ints" in {
    Wrap(2) > Wrap(1) shouldBe true
    Wrap(2) > Wrap(2) shouldBe false
    Wrap(2) >= Wrap(2) shouldBe true
  }
}

object OrdSyntaxSpec {

  final case class Wrap[+T](inner: T) extends AnyVal

  object Wrap {
    implicit def newOrd[T](implicit O: Ord[T]): Ord[Wrap[T]] =
      (a, b) => O.compare(a.inner, b.inner)

    implicit def newPartialEq[T](implicit O: PartialEquality[T]): PartialEquality[Wrap[T]] =
      (a, b) => O.equal(a.inner, b.inner)
  }

}
