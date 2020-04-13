package finder.domain

trait ConfigLoader[F[_], A] {
  def load: F[A]
}
