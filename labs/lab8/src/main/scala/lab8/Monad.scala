package lab8

import scala.concurrent.{ExecutionContext, Future}

trait Monad[F[+ _]] extends Functor[F] {
  /**
   * There are three monad laws:
   * • Left identity:
   *    Monad[F].pure(i).flatMap(f) == f(i)
   * • Right identity:
   *    j.flatMap(Monad[F].pure) == j
   * • Associativity:
   *    j.flatMap(f).flatMap(g) ==
   *    j.flatMap { x => f(x).flatMap(g) }
   */

  def pure[T](v: T): F[T]

  def flatMap[A, B](f: A => F[B])(v: F[A]): F[B]

  def flatten[T](v: F[F[T]]): F[T] = flatMap(identity[F[T]])(v)

  override def fmap[A, B](f: A => B)(v: F[A]): F[B] = flatMap((i: A) => pure(f(i)))(v)
}



object Monad {
  def apply[F[+ _]](implicit instance: Monad[F]): Monad[F] = instance

  implicit def listMonad: Monad[List] = new Monad[List] {
    override def pure[T](v: T): List[T] = List(v)

    override def flatMap[A, B](f: A => List[B])(v: List[A]): List[B] =
      v.flatMap(f)
  }


  implicit def optionMonad: Monad[Option] = new Monad[Option] {
    override def pure[T](v: T): Option[T] = Some(v)

    override def flatMap[A, B](f: A => Option[B])(v: Option[A]): Option[B] =
      v.flatMap(f)
  }

  implicit def vectorMonad: Monad[Vector] = new Monad[Vector] {
    override def pure[T](v: T): Vector[T] = Vector(v)

    override def flatMap[A, B](f: A => Vector[B])(v: Vector[A]): Vector[B] =
      v.flatMap(f)
  }

  type ErrorOrT[+T] = Either[Error, T]

  implicit def eitherMonad: Monad[ErrorOrT] = new Monad[ErrorOrT] {
    override def pure[T](v: T): ErrorOrT[T] = Right(v)

    override def flatMap[A, B](f: A => ErrorOrT[B])(v: ErrorOrT[A]): ErrorOrT[B] = v match {
      case Left(value) => Left(value)
      case Right(value) => f(value)
    }
  }

  type Function[-I, +O] = I => O

  implicit def f1Monad[I]: Monad[Function[I, +*]] = new Monad[Function[I, +*]] {
    override def pure[T](v: T): Function[I, T] = _ => v

    override def flatMap[A, B](f: A => Function[I, B])(v: Function[I, A]): Function[I, B] =
      (arg: I) => f(v(arg))(arg)
  }

  implicit def idMonad: Monad[Id] = new Monad[Id] {
    override def pure[T](v: T): Id[T] = v

    override def flatMap[A, B](f: A => Id[B])(v: Id[A]): Id[B] = f(v)
  }

  implicit def futureMonad(implicit ec: ExecutionContext): Monad[Future] = new Monad[Future] {
    override def pure[T](v: T): Future[T] = Future.successful(v)

    override def flatMap[A, B](f: A => Future[B])(v: Future[A]): Future[B] =
      v.flatMap(f)
  }

//  type InpToFuture[-I, +O] = I => Future[O]
//
//  //  implicit def f1Monad[I]: Monad[Function[I, +*]] = new Monad[Function[I, +*]] {
//  implicit def futureFuncMonad[I](implicit ec: ExecutionContext): Monad[InpToFuture[I, +*]] =
//    new Monad[InpToFuture[I, +*]] {
//      override def pure[T](v: T): InpToFuture[I, T] = (i: I) => Future(v)
//
//      override def flatMap[A, B](f: A => InpToFuture[I, B])(v: InpToFuture[I, A]): InpToFuture[I, B] =
//
//    }
}
