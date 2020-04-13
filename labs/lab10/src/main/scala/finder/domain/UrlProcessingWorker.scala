package finder.domain

import finder.model.{Keyword, KeywordCount, Url}

trait UrlProcessingWorker[F[_]] {
  def start(urlsPath: String,
            resultPath: String,
            keywords: List[Keyword])
           (handler: Url => F[List[KeywordCount]]): F[Unit]
}
