package kek

import akka.stream.alpakka.csv.scaladsl.CsvFormatting
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString

object StreamStages {
  // No need to test this – not implemented by us
  val formatter: Flow[Vector[String], ByteString, Any] = CsvFormatting.format()

  // No need to test this also – too simple
  def toHeader(columns: Vector[String]): Source[Vector[String], Any] = Source.single(columns)

  def filter(symbol: Option[String]): Flow[Entry, Entry, Any] = symbol match {
    case None => Flow[Entry]
    case Some(symbol) => Flow[Entry].filter(_.symbol == symbol)
  }
}
