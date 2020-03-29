package dogs.syntax

import dogs.Monad
import dogs.syntax.monadic._

package object foldable {

  implicit final class TraverseSyntax[T](private val inner: List[T]) extends AnyVal {
    def traverse[F[+_] : Monad, O](f: T => F[O]): F[List[O]] =
      inner.foldLeft(List.empty[O].pure[F]) { (acc, a) =>
        for {
          xs <- acc: F[List[O]]
          x <- f(a): F[O]
        } yield xs :+ x
      }
  }

  implicit final class SequenceSyntax[F[+_], T](private val inner: List[F[T]]) extends AnyVal {
    def sequence(implicit M: Monad[F]): F[List[T]] =
      inner.foldLeft(List.empty[T].pure[F]) { (acc, a) =>
        for {
          xs <- acc: F[List[T]]
          x <- a: F[T]
        } yield xs :+ x
      }
  }

}
