package finder.domain

import cats.effect.{Bracket, Resource}
import cats.implicits._
import finder.model.ApplicationConfig

trait Application[F[_]] {
  def start: F[Unit]
}

object Application {
  implicit def deriveDefaultApplication[F[_]](implicit
                                              ctxProvider: ApplicationContextProvider[F],
                                              bracket: Bracket[F, Throwable]): Application[F] = new DefaultApplication[F]
}

case class ApplicationContext[F[_]](
  configLoader: ConfigLoader[F, ApplicationConfig],
  keywordLoader: KeywordLoader[F],
  processor: UrlProcessor[F],
  worker: UrlProcessingWorker[F]
)

trait ApplicationContextProvider[F[_]] {
  def ctx: Resource[F, ApplicationContext[F]]
}

class DefaultApplication[F[_]](implicit
                               ctxProvider: ApplicationContextProvider[F],
                               bracket: Bracket[F, Throwable]) extends Application[F]{
  def start: F[Unit] =
    ctxProvider.ctx.use(ctxUsage)

  // Internal

  private def ctxUsage(ctx: ApplicationContext[F]): F[Unit] = {
    for {
      cfg      <- ctx.configLoader.load
      keywords <- ctx.keywordLoader.load(cfg.keywordsPath)
      _        <- ctx.worker.start(cfg.urlsPath, cfg.resultPath, keywords)(ctx.processor.process(keywords, _))
    } yield ()
  }
}
