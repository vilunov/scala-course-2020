package kek

import java.time.LocalDateTime

/**
 * Works following example from https://towardsdatascience.com/trading-toolbox-02-wma-ema-62c22205e2a9
 */
case class ContinuousMetrics(timestamp: LocalDateTime, symbol: String, sma: Double, ema: Double) {
  def toVector: Vector[String] = Vector(timestamp.toString, symbol, sma.toString, ema.toString)
}

object ContinuousMetrics {
  val header: Vector[String] = Vector("timestamp", "symbol", "SMA", "EMA")


  def continuousMetricsSliding(window: Seq[InstantMetrics]): ContinuousMetrics = {
    val alpha = 2.0 / (1 + window.length)
    val vwap = window.map(_.vwapMid)
    val sma = vwap.sum / window.length
    val ema = vwap.fold(0.0) {
      (a, b) => {
        (1 - alpha) * a + alpha * b
      }
    }
    ContinuousMetrics(window(0).timestamp, window(0).symbol, sma, ema)
  }
}
