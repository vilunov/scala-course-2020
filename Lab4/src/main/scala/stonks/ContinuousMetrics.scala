package stonks

import java.time.LocalDateTime

final case class ContinuousMetrics(startTimestamp: LocalDateTime,
                                   endTimestamp: LocalDateTime,
                                   symbol: String,
                                   sma: Double,
                                   ema: Double) {
  def toVector: Vector[String] =
    Vector(startTimestamp.toString, endTimestamp.toString, symbol, sma.toString, ema.toString)
}
