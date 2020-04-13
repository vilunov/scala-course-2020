package finder.domain

import finder.model.{Keyword, KeywordCount, Url}

trait UrlProcessor[F[_]] {
  def process(keywords: List[Keyword], url: Url): F[List[KeywordCount]]
}
