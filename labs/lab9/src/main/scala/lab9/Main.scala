package lab9

object Main extends App {

  val list1 = List(1, 2, 3)
  val list2 = Functor[List].map(list1)(_ * 2)// list2: List[Int] = List(2, 4, 6)

  val option1 = Option(123)
  val option2 = Functor[Option].map(option1)(_.toString) // option2: Option[String] = Some(123)

  val opt1 = Monad[Option].pure(3) // opt1: Option[Int] = Some(3)
}
