package maxim.salo

trait Ordering[-T] {
  // Returns Ordering.Greater if a > b
  // Returns Ordering.Equal if a == b
  // Returns Ordering.Lesser if a < b
  def compare(a: T, b: T): Ordering.Result
}

object Ordering {

  sealed trait Result

  object Greater extends Result

  object Equal extends Result

  object Lesser extends Result

  def max[T](ordering: Ordering[T])(first: T, other: T*): T = {
    other.foldLeft(first)((a, b) => if (Ordering.Greater == ordering.compare(a, b)) a else b)
  }
}

/**
 * 5 == 5
 * 3 < 6
 * 4 <= 4, 4 <= 4 => 4 == 4
 * 3 < 5, 5 < 7 => 3 < 7
 */
object IntOrdering extends Ordering[Int] {

  override def compare(a: Int, b: Int): Ordering.Result = {
    val res = a - b
    if (res == 0) Ordering.Equal
    else if (res > 0) Ordering.Greater
    else Ordering.Lesser
  }
}

/**
 * (2, 3) == (2, 3)
 * (2) < (2, 3)
 * (4, 2) <= (6, 3), (6, 3) <= (4, 2) => (4, 2) == (6, 3)
 * (2) < (3, 5), (3, 5) < (4, 6, 8) => (2) < (4, 6, 8)
 */
object ListOrdering extends Ordering[List[Any]] {

  override def compare(a: List[Any], b: List[Any]): Ordering.Result = {
    val res = a.length - b.length
    if (res == 0) Ordering.Equal
    else if (res > 0) Ordering.Greater
    else Ordering.Lesser
  }
}

/**
 * "lol" == "lol"
 * "aaa" < "lol"
 * "aaa" <= "aaa", "aaa" <= "aaa" => "aaa" == "aaa"
 * "aaa" < "bbb", "bbb" < "ccc" => "aaa" < "ccc"
 */
object StringOrdering extends Ordering[String] {

  override def compare(a: String, b: String): Ordering.Result = {
    val res = a.compareTo(b)
    if (res == 0) Ordering.Equal
    else if (res > 0) Ordering.Greater
    else Ordering.Lesser
  }
}

/**
 * (1, 2) == (1, 2)
 * (1, 2) < (3, 1)
 * (1, 2) <= (1, 2), (1, 2) <= (1, 2) => (1, 2) == (1, 2)
 * (1, 2) < (3, 4), (3, 4) < (7, 1) => (1, 2) < (7, 1)
 */
class FirstThenSecond[A, B](orderingA: Ordering[A], orderingB: Ordering[B]) extends Ordering[(A, B)] {

  override def compare(a: (A, B), b: (A, B)): Ordering.Result = {
    val cmpA = orderingA.compare(a._1, b._1)
    if (Ordering.Equal == cmpA) {
      val cmpB = orderingB.compare(a._2, b._2)
      if (Ordering.Equal == cmpB) Ordering.Equal
      else if (Ordering.Greater == cmpB) Ordering.Greater
      else Ordering.Lesser
    }
    else if (Ordering.Greater == cmpA) Ordering.Greater
    else Ordering.Lesser
  }
}

/**
 * (1, 2) == (1, 2)
 * (4, 2) < (1, 5)
 * (1, 2) <= (1, 2), (1, 2) <= (1, 2) => (1, 2) == (1, 2)
 * (1, 2) < (3, 4), (3, 4) < (1, 7) => (1, 2) < (1, 7)
 */
class SecondThenFirst[A, B](orderingA: Ordering[A], orderingB: Ordering[B]) extends Ordering[(A, B)] {

  override def compare(a: (A, B), b: (A, B)): Ordering.Result = {
    val cmpB = orderingB.compare(a._2, b._2)
    if (Ordering.Equal == cmpB) {
      val cmpA = orderingA.compare(a._1, b._1)
      if (Ordering.Equal == cmpA) Ordering.Equal
      else if (Ordering.Greater == cmpA) Ordering.Greater
      else Ordering.Lesser
    }
    else if (Ordering.Greater == cmpB) Ordering.Greater
    else Ordering.Lesser
  }
}
