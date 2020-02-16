package kek

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


object Kek extends App {

  import StreamStages._

  implicit val actors: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = actors.dispatcher

  val source: Source[Entry, Any] = FileIO
    .fromPath(Paths.get("snapshots.csv"))
    .via(CsvParsing.lineScanner())
    .map(i => Entry.fromLine(i.map(_.utf8String)))
    .collect { case Some(v) => v }

  val route: Route = (path("instant-metrics") & parameter("symbol".as[String].?)) { symbol =>
    get {
      val stream = source
        .via(filter(symbol))
        .via(instantMetrics)
        .map(_.toVector)
        .prepend(header)
        .via(formatter)
      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, stream))
    }
  }

  for {
    binding <- Http().bindAndHandle(route, "localhost", 8080)
    _ = sys.addShutdownHook {
      for {
        _ <- binding.terminate(Duration(5, TimeUnit.SECONDS))
        _ <- actors.terminate()
      } yield ()
    }
  } yield ()
}
