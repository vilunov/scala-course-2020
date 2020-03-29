package dogs

import scala.concurrent.{ExecutionContext, Future}

final case class Wrap[-I, +O](run: I => Future[O]) {
  def apply(v: I): Future[O] = run(v)
}

object Wrap {
  implicit def monad[I](implicit executionContext: ExecutionContext): Monad[Wrap[I, +*]] = new Monad[Wrap[I, +*]] {
    override def pure[T](v: T): Wrap[I, T] = Wrap { _ => Future.successful(v) }

    override def flatMap[A, B](f: A => Wrap[I, B], v: Wrap[I, A]): Wrap[I, B] = Wrap { arg: I =>
      v(arg).flatMap(i => f(i)(arg))
    }
  }
}
