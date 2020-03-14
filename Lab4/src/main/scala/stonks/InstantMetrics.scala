package stonks

import java.time.LocalDateTime

final case class InstantMetrics(timestamp: LocalDateTime,
                                symbol: String,
                                midpoint: Double,
                                vwapAsks: Double,
                                vwapBids: Double,
                                vwapMidpoint: Double) {
  def toVector: Vector[String] =
    Vector(timestamp.toString, symbol, midpoint.toString, vwapAsks.toString, vwapBids.toString, vwapMidpoint.toString)
}
