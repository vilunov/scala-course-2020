package dogs

import scala.concurrent.{ExecutionContext, Future}

trait Monad[F[+_]] extends Functor[F] {
  def pure[T](v: T): F[T]

  def flatMap[A, B](f: A => F[B], v: F[A]): F[B]

  def flatten[T](v: F[F[T]]): F[T] = flatMap(identity[F[T]], v)

  override def fmap[A, B](f: A => B, v: F[A]): F[B] = flatMap[A, B](i => pure(f(i)), v)
}

object Monad {
  @inline def apply[F[+_]](implicit M: Monad[F]): Monad[F] = M

  implicit val optionMonad: Monad[Option] = new Monad[Option] {
    def pure[T](v: T): Option[T] = Option(v)

    def flatMap[A, B](f: A => Option[B], v: Option[A]): Option[B] = v.flatMap(f)
  }

  implicit val vectorMonad: Monad[Vector] = new Monad[Vector] {
    override def pure[T](v: T): Vector[T] = Vector(v)

    override def flatMap[A, B](f: A => Vector[B], v: Vector[A]): Vector[B] = v.flatMap(f)
  }

  implicit val idMonad: Monad[Id] = new Monad[Id] {
    def pure[T](v: T): T = v

    def flatMap[A, B](f: A => B, v: A): B = f(v)

    override def fmap[A, B](f: A => B, v: A): B = f(v)

    override def flatten[T](v: T): T = v
  }

  implicit def futureMonad(implicit executionContext: ExecutionContext): Monad[Future] = new Monad[Future] {
    override def pure[T](v: T): Future[T] = Future.successful(v)

    override def flatMap[A, B](f: A => Future[B], v: Future[A]): Future[B] = v.flatMap(f)(executionContext)
  }

  // Refer to `Functor` for explanation of type casts
  implicit def eitherMonad[L]: Monad[Either[L, +*]] = UniversalEitherMonad.asInstanceOf[Monad[Either[L, +*]]]

  implicit def function1Monad[L]: Monad[L => +*] = UniversalFunction1Monad.asInstanceOf[Monad[L => +*]]

  private object UniversalEitherMonad extends Monad[Either[Any, +*]] {
    override def pure[T](v: T): Either[Any, T] = Right(v)

    override def flatMap[A, B](f: A => Either[Any, B], v: Either[Any, A]): Either[Any, B] =
      v.flatMap(f)
  }

  private object UniversalFunction1Monad extends Monad[Any => +*] {
    override def pure[T](v: T): Any => T = _ => v

    override def flatMap[A, B](f: A => Any => B, v: Any => A): Any => B = { arg =>
      f(v(arg))(arg)
    }
  }

}
