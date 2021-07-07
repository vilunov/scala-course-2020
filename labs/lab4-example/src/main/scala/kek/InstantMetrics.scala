package kek

import java.time.LocalDateTime

final case class InstantMetrics(timestamp: LocalDateTime, symbol: String, midPoint: Double,
                                vwapAsk: Double, vwapBid: Double) {
  val vwapMid: Double = (vwapAsk + vwapBid) / 2

  def toVector: Vector[String] = Vector(timestamp.toString, symbol, midPoint.toString,
    vwapAsk.toString, vwapBid.toString, vwapMid.toString)
}

object InstantMetrics {
  val header: Vector[String] = Vector("timestamp", "symbol", "mid_point", "vwap_ask", "vwap_bid", "vwap_mid_point")

  /**
   * Performs soring of bids, asks before computing by itself
   */
  def compute(vwapCap: Double)(entry: Entry): InstantMetrics = {
    val sortedAsks = entry.asks.sortWith(_._1 < _._1) // Ascending 5-4-3-...
    val sortedBids = entry.bids.sortWith(_._1 > _._1) // Descending 6-7-8-... Because we count same metrics from left side for both
    val meetingPoint = (entry.bids.head._1 + entry.asks.head._1) / 2
    val vwapAsk = vwap(sortedAsks, vwapCap)
    val vwapBid = vwap(sortedBids, vwapCap)
    InstantMetrics(entry.timestamp, entry.symbol, meetingPoint, vwapAsk, vwapBid)
  }

  /**
   * @param a   - ORDERED vector of pairs (price, volume). Works from left side (0 -> len-1)
   * @param cap - maximum capability param from equation
   * @return vwap metric
   */
  def vwap(a: Vector[(Double, Int)], cap: Double): Double = {
    var sum = 0.0
    var prod = 0.0
    for ((ask, vol) <- a) {
      if (sum + vol > cap) {
        return prod / sum
      }
      sum += vol
      prod += ask * vol
    }
    prod / sum
  }
}

