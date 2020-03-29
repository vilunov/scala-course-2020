package dogs

trait Functor[F[+_]] {
  def fmap[A, B](f: A => B, v: F[A]): F[B]
}

object Functor {
  @inline def apply[F[+_]](implicit F: Functor[F]): Functor[F] = F

  @inline implicit def fromMonad[F[+_]](implicit M: Monad[F]): Functor[F] = M

  // Type casting of instances is a trick used to avoid unnecessary allocations of objects.
  // This code wouldn't type check without casting,  but we know for sure that `UniversalMapFunctor` is a correct
  // functor instance for maps with any keys, thus we can cast it to the required functor type.
  // Casting does not create a new object.
  implicit def mapFunctor[K]: Functor[Map[K, +*]] = UniversalMapFunctor.asInstanceOf[Functor[Map[K, +*]]]

  private object UniversalMapFunctor extends Functor[Map[Any, +*]] {
    override def fmap[A, B](f: A => B, v: Map[Any, A]): Map[Any, B] = v.map { case (key, values) =>
      key -> f(values)
    }
  }

}
