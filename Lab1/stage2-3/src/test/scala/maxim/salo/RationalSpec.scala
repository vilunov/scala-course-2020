package maxim.salo

import org.scalatest.{FlatSpec, Matchers}

class RationalSpec extends FlatSpec with Matchers {

  "==" should "be correct for 2/1 and 4/2" in {
    val isEqual = Rational(2, 1) == Rational(4, 2)
    isEqual shouldBe true
  }

  "+" should "be correct for (2/1 + 2) and 4/1" in {
    val res     = Rational(2, 1) + 2
    val isEqual = res == Rational(4, 1)
    isEqual shouldBe true
  }

  "/" should "be correct for (9/3 / 5) and 3/5" in {
    val res     = Rational(9, 3) / 5
    val isEqual = res == Rational(3, 5)
    isEqual shouldBe true
  }

  "val Rational(b, c) = Rational(4, 2)" should "return b = 2, c = 1" in {
    val a              = Rational(4, 2)
    val Rational(b, c) = a
    b shouldEqual 2
    c shouldEqual 1
  }

  "unary_-" should "be correct for -UserBalance(3/2, 5/3, 8/7) and UserBalance(-3/2, -5/3, -8/7)" in {
    val ub = UserBalance(Rational(3, 2), Rational(5, 3), Rational(8, 7))
    -ub shouldEqual UserBalance(-Rational(3, 2), -Rational(5, 3), -Rational(8, 7))
  }

  "+" should "be correct for (UserBalance(3/2, 5/3, 8/3) + UserBalance(5/2, 5/4, 7/3)) and UserBalance(4/1, 35/12, 5/1)" in {
    val ub1 = UserBalance(Rational(3, 2), Rational(5, 3), Rational(8, 3))
    val ub2 = UserBalance(Rational(5, 2), Rational(5, 4), Rational(7, 3))
    ub1 + ub2 shouldEqual UserBalance(Rational(4, 1), Rational(35, 12), Rational(5, 1))
  }

  "-" should "be correct for (UserBalance(3/2, 5/3, 8/3) + UserBalance(5/2, 5/4, 7/3)) and UserBalance(-1/1, 5/12, 1/3)" in {
    val ub1 = UserBalance(Rational(3, 2), Rational(5, 3), Rational(8, 3))
    val ub2 = UserBalance(Rational(5, 2), Rational(5, 4), Rational(7, 3))
    ub1 - ub2 shouldEqual UserBalance(-Rational(1, 1), Rational(5, 12), Rational(1, 3))
  }
}
