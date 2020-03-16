package dogs

trait Semigroup[T]

trait Monoid[T] extends Semigroup[T]

object Monoid {
  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] = ???
  implicit def equalityMonoid[T]: Monoid[Equality[T]] = ???
}

trait CommutativeSemigroup[T] extends Semigroup[T]

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {
  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] = ???
}

