package finder.domain

import finder.model.Keyword

trait KeywordLoader[F[_]] {
  def load(path: String): F[List[Keyword]]
}
