package kek

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import scala.util.Try

final case class Entry(timestamp: LocalDateTime, symbol: String, bids: Vector[(Float, Int)], asks: Vector[(Float, Int)])

object Entry {
  private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def fromLine(line: List[String]): Option[Entry] = {
    line match {
      case timestamp +: symbol +: bidsAndAsks if bidsAndAsks.length == 100 =>
        for {
          time <- Try(LocalDateTime.from(format.parse(timestamp))).toOption
          (costs, volumes) = bidsAndAsks.zipWithIndex.partitionMap {
            case (v, index) if index % 2 == 0 => Left(v)
            case (v, _) => Right(v)
          }
          parsedCosts <- OptionUtils.sequence(costs.map(_.toFloatOption))
          parsedVolumes <- OptionUtils.sequence(volumes.map(_.toIntOption))
          (bids, asks) = parsedCosts.zip(parsedVolumes).splitAt(25)
        } yield Entry(time, symbol, bids.toVector, asks.toVector)
      case _ => None
    }
  }
}
