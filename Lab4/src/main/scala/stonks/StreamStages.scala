package stonks

import akka.stream.alpakka.csv.scaladsl.CsvFormatting
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString

object StreamStages {
  // No need to test this – not implemented by us
  val formatter: Flow[Vector[String], ByteString, Any] = CsvFormatting.format()

  // No need to test this also – too simple
  val instantHeader: Source[Vector[String], Any] =
    Source.single(Vector("timestamp", "symbol", "midpoint", "vwap_asks", "vwap_bids", "vwap_midpoint"))
  val continuousHeader:
    Source[Vector[String], Any] = Source.single(Vector("start_timestamp", "end_timestamp", "symbol", "sma", "ema"))

  def filter(symbolOpt: Option[String]): Flow[Entry, Entry, Any] = symbolOpt match {
    case None => Flow[Entry]
    case Some(symbol) => Flow[Entry].filter(_.symbol == symbol)
  }

  def instantMetrics(cOpt: Option[Int]): Flow[Entry, InstantMetrics, Any] = Flow[Entry].map {
    entry =>
      val midpoint = (entry.bids.last._1 + entry.asks.head._1) / 2
      val c = cOpt match {
        case None => 1_000_000
        case Some(x) => x
      }
      val vwapAsks = vwap(entry.asks, OrderType.Ask, c)
      val vwapBids = vwap(entry.bids, OrderType.Bid, c)
      val vwapMidpoint = (vwapAsks + vwapBids) / 2

      InstantMetrics(entry.timestamp, entry.symbol, midpoint, vwapAsks, vwapBids, vwapMidpoint)
  }

  def vwap(orders: Seq[(Double, Int)],
           orderType: OrderType.Value,
           c: Int): Double = orderType match {
    case OrderType.Ask => if (orders.isEmpty) 0 else vwap(orders, orderType, 0, 0, c)
    case OrderType.Bid => if (orders.isEmpty) 0 else vwap(orders.reverse, orderType, 0, 0, c)
  }

  @scala.annotation.tailrec
  private def vwap(orders: Seq[(Double, Int)],
                   orderType: OrderType.Value,
                   numerator: Double,
                   denominator: Int,
                   c: Int): Double = {
    if (orders.isEmpty)
      numerator / denominator
    else {
      val curPrice = orders.head._1
      val curVol = orders.head._2
      if (denominator + curVol > c)
        if (denominator > 0) numerator / denominator else curPrice
      else
        vwap(orders.tail, orderType, numerator + curPrice * curVol, denominator + curVol, c)
    }
  }

  def continuousMetrics(n: Int): Flow[InstantMetrics, ContinuousMetrics, Any] = Flow[InstantMetrics].sliding(n).map {
    window =>
      val startTimestamp = window.head.timestamp
      val endTimestamp = window.last.timestamp
      val vwapMidpoints = window.map(_.vwapMidpoint)
      val smaValue = sma(vwapMidpoints)
      val emaValue = ema(vwapMidpoints)

      ContinuousMetrics(startTimestamp, endTimestamp, window.head.symbol, smaValue, emaValue)
  }

  def sma(vwapMidpoints: Seq[Double]): Double = {
    if (vwapMidpoints.nonEmpty)
      vwapMidpoints.sum / vwapMidpoints.size
    else
      0
  }

  def ema(vwapMidpoints: Seq[Double]): Double = {
    ema(vwapMidpoints, 0, 2.0 / (vwapMidpoints.size + 1))
  }

  @scala.annotation.tailrec
  private def ema(vwapMidpoints: Seq[Double],
                  prev: Double,
                  a: Double): Double = {
    if (vwapMidpoints.isEmpty)
      prev
    else {
      val next = a * vwapMidpoints.head + (1.0 - a) * prev
      ema(vwapMidpoints.tail, next, a)
    }
  }
}
