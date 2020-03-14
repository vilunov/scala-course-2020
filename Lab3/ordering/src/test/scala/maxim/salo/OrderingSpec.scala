package maxim.salo

import org.scalatest.{FlatSpec, Matchers}

class OrderingSpec extends FlatSpec with Matchers {

  "IntOrdering" should "be correct for (5, 1, 2, 3)" in {
    val res = Ordering.max(IntOrdering)(5, 1, 2, 3)
    res shouldEqual 5
  }

  "IntOrdering" should "be correct for (55, 66, 1212, 100000)" in {
    val res = Ordering.max(IntOrdering)(55, 66, 1212, 100000)
    res shouldEqual 100000
  }

  "ListOrdering" should "be correct for (List(1), List(2, 3), List(4))" in {
    val res = Ordering.max(ListOrdering)(List(1), List(2, 3), List(4))
    res shouldBe List(2, 3)
  }

  "ListOrdering" should "be correct for (List(6, 7), List(3), List(1, 1, 1))" in {
    val res = Ordering.max(ListOrdering)(List(6, 7), List(3), List(1, 1, 1))
    res shouldBe List(1, 1, 1)
  }

  "StringOrdering" should "be correct for (\"kek\", \"lol\", \"aaa\")" in {
    val res = Ordering.max(StringOrdering)("kek", "lol", "aaa")
    res shouldBe "lol"
  }

  "StringOrdering" should "be correct for (\"kek\", \"l\", \"aaaaaaaaaaa\")" in {
    val res = Ordering.max(StringOrdering)("kek", "l", "aaaaaaaaaaa")
    res shouldBe "l"
  }

  "FirstThenSecond" should "be correct for ((1, 1), (1, 2))" in {
    val res = Ordering.max(new FirstThenSecond[Int, Int](IntOrdering, IntOrdering))((1, 1), (1, 2))
    res shouldBe(1, 2)
  }

  "FirstThenSecond" should "be correct for ((5, 1), (1, 9999999))" in {
    val res = Ordering.max(new FirstThenSecond[Int, Int](IntOrdering, IntOrdering))((5, 1), (1, 9999999))
    res shouldBe(5, 1)
  }

  "FirstThenSecond" should "be correct for ((1, 1), (0, 2))" in {
    val res = Ordering.max(new FirstThenSecond[Int, Int](IntOrdering, IntOrdering))((1, 1), (0, 2))
    res shouldBe(1, 1)
  }

  "SecondThenFirst" should "be correct for ((3, 1), (1, 2))" in {
    val res = Ordering.max(new SecondThenFirst[Int, Int](IntOrdering, IntOrdering))((3, 1), (1, 2))
    res shouldBe(1, 2)
  }

  "SecondThenFirst" should "be correct for ((99999, 1), (1, 5))" in {
    val res = Ordering.max(new SecondThenFirst[Int, Int](IntOrdering, IntOrdering))((3, 1), (1, 2))
    res shouldBe(1, 2)
  }

  "SecondThenFirst" should "be correct for ((3, 1), (2, 1))" in {
    val res = Ordering.max(new SecondThenFirst[Int, Int](IntOrdering, IntOrdering))((3, 1), (2, 1))
    res shouldBe(3, 1)
  }
}
