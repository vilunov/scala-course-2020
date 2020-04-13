package finder.domain

import finder.model.{Keyword, KeywordCount}

trait KeywordCounter[F[_], Ctx] {
  def count(keyword: Keyword, context: Ctx): F[KeywordCount]
}
