package maxim.salo

object Rational {

  def apply(n: Int, d: Int): Rational = {
    require(d != 0)
    val gcdVal = gcd(n, d)
    new Rational(n / gcdVal, d / gcdVal)
  }

  def unapply(arg: Rational): Some[(Int, Int)] = Some(arg.nominator, arg.denominator)

  @scala.annotation.tailrec
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
}

class Rational private (val nominator: Int, val denominator: Int) {

  def this(n: Int) = this(n, 1)

  def +(that: Rational): Rational =
    Rational(nominator * that.denominator + that.nominator * denominator, denominator * that.denominator)

  def +(i: Int): Rational = this + new Rational(i)

  def -(that: Rational): Rational =
    Rational(nominator * that.denominator - that.nominator * denominator, denominator * that.denominator)

  def -(i: Int): Rational = this - new Rational(i)

  def *(that: Rational): Rational = Rational(nominator * that.nominator, denominator * that.denominator)

  def *(i: Int): Rational = this * new Rational(i)

  def /(that: Rational): Rational = Rational(nominator * that.denominator, denominator * that.nominator)

  def /(i: Int): Rational = this / new Rational(i)

  def ==(that: Rational): Boolean = nominator == that.nominator && denominator == that.denominator

  def unary_-(): Rational = Rational(-this.nominator, this.denominator)

  override def toString: String = nominator + "/" + denominator
}
