package finder.domain

import finder.model.Url

trait HttpClient[F[_], Ctx] {
  def get(url: Url): F[Option[Ctx]]
}
