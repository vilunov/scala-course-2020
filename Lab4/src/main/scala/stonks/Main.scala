package stonks

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.alpakka.csv.scaladsl._
import akka.stream.scaladsl._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration


object Main extends App {

  import StreamStages._

  implicit val actors: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = actors.dispatcher

  val source: Source[Entry, Any] = FileIO
    .fromPath(Paths.get("snapshots.csv"))
    .via(CsvParsing.lineScanner())
    .map(i => Entry.fromLine(i.map(_.utf8String)))
    .collect { case Some(v) => v }

  val routeInstant: Route = (path("instant-metrics")
    & parameter("symbol".as[String].?)
    & parameter("c".as[Int].?)) {
    (symbol, c) =>
      get {
        validate(c.forall(_ >= 0), "C must be non-negative") {
          val stream = source
            .via(filter(symbol))
            .via(instantMetrics(c))
            .map(_.toVector)
            .prepend(instantHeader)
            .via(formatter)
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, stream))
        }
      }
  }

  val routeContinuous: Route = (path("continuous-metrics")
    & parameter("symbol".as[String])
    & parameter("c".as[Int].?)
    & parameter("n".as[Int])) {
    (symbol, c, n) =>
      get {
        (validate(c.forall(_ >= 0), "C must be non-negative")
          & validate(n > 0, "N must be greater than 0")) {
          val stream = source
            .via(filter(Some(symbol)))
            .via(instantMetrics(c))
            .via(continuousMetrics(n))
            .map(_.toVector)
            .prepend(continuousHeader)
            .via(formatter)
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, stream))
        }
      }
  }

  for {
    binding <- Http().bindAndHandle(routeInstant ~ routeContinuous, "localhost", 8080)
    _ = sys.addShutdownHook {
      for {
        _ <- binding.terminate(Duration(5, TimeUnit.SECONDS))
        _ <- actors.terminate()
      } yield ()
    }
  } yield ()
}
