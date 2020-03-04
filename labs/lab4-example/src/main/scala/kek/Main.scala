package kek

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route, ValidationRejection}
import akka.stream.alpakka.csv.scaladsl._
import akka.stream.scaladsl._
import kek.StreamStages._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import ContinuousMetrics._


object MainRouter {
  val generalExceptionHandler = ExceptionHandler {
    case err: Throwable =>
      complete(HttpResponse(400, entity = err.getMessage))
  }

  val source: Source[Entry, Any] = FileIO
    .fromPath(Paths.get("snapshots.csv"))
    .via(CsvParsing.lineScanner())
    .map(i => Entry.fromLine(i.map(_.utf8String)))
    .collect { case Some(v) => v }

  val InstantMetricsRoute: Route = (path("instant-metrics")
    & parameters("symbol".as[String].?, "cap".as[Double] ? 1e6)) { (symbol, cap) =>
    get {
      validate(cap > 0, s"Capability (cap) must be > 0. $cap provided.")
      val stream = source
        .via(filter(symbol))
        .map(InstantMetrics.compute(cap))
        .map(_.toVector)
        .prepend(toHeader(InstantMetrics.header))
        .via(formatter)
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, stream))
    }
  }

  val ContinuousMetricsRoute: Route = (path("continuous-metrics")
    & parameters("symbol".as[String].?, "window-size".as[Integer] ? 5, "cap".as[Double] ? 1e6)) {
    (symbol, windowSize, cap) =>
      validate(windowSize > 0, s"Window size must be > 0. $windowSize provided.")
      validate(cap > 0, s"Capability (cap) must be > 0. $cap provided.")
      get {
        val stream = source
          .via(filter(symbol))
          .map(InstantMetrics.compute(cap))
          .sliding(windowSize)
          .map(continuousMetricsSliding)
          .map(_.toVector)
          .prepend(toHeader(ContinuousMetrics.header))
          .via(formatter)
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, stream))
      }
  }

  val routes: Route = handleExceptions(generalExceptionHandler) {
    InstantMetricsRoute ~ ContinuousMetricsRoute
  }
}

object Main extends App {
  implicit val actors: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = actors.dispatcher

  for {
    binding <- Http().bindAndHandle(MainRouter.routes, "localhost", 8080)
    _ = sys.addShutdownHook {
      for {
        _ <- binding.terminate(Duration(5, TimeUnit.SECONDS))
        _ <- actors.terminate()
      } yield ()
    }
  } yield ()
}
