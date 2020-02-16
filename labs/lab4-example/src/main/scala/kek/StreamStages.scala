package kek

import akka.stream.alpakka.csv.scaladsl.CsvFormatting
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString

object StreamStages {
  // No need to test this – not implemented by us
  val formatter: Flow[Vector[String], ByteString, Any] = CsvFormatting.format()

  // No need to test this also – too simple
  val header: Source[Vector[String], Any] = Source.single(Vector("timestamp", "symbol", "meeting_point"))

  def filter(symbol: Option[String]): Flow[Entry, Entry, Any] = symbol match {
    case None => Flow[Entry]
    case Some(symbol) => Flow[Entry].filter(_.symbol == symbol)
  }

  val instantMetrics: Flow[Entry, Metrics, Any] = Flow[Entry].map { entry =>
    val meetingPoint = (entry.bids.last._1 + entry.asks.head._1) / 2
    Metrics(entry.timestamp, entry.symbol, meetingPoint)
  }
}
