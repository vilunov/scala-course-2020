package stonks

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StreamStagesSpec extends AnyFlatSpec with Matchers with OptionValues {

  "vwap" should "be correct for bids" in {
    val orders = Vector((99.0, 10000), (100.0, 20000), (101.0, 10000), (102.0, 20000))
    val res = StreamStages.vwap(orders, OrderType.Bid, 1000000)
    res shouldEqual (99.0 * 10000 + 100.0 * 20000 + 101.0 * 10000 + 102.0 * 20000) / (10000 + 20000 + 10000 + 20000)
  }

  "vwap" should "be correct for bids and c" in {
    val orders = Vector((99.0, 10000), (100.0, 20000), (101.0, 10000), (102.0, 20000))
    val res = StreamStages.vwap(orders, OrderType.Bid, 30000)
    res shouldEqual (101.0 * 10000 + 102.0 * 20000) / (10000 + 20000)
  }

  "vwap" should "be correct for asks" in {
    val orders = Vector((99.0, 10000), (100.0, 20000), (101.0, 10000), (102.0, 20000))
    val res = StreamStages.vwap(orders, OrderType.Ask, 1000000)
    res shouldEqual (99.0 * 10000 + 100.0 * 20000 + 101.0 * 10000 + 102.0 * 20000) / (10000 + 20000 + 10000 + 20000)
  }

  "vwap" should "be correct for asks anc c" in {
    val orders = Vector((99.0, 10000), (100.0, 20000), (101.0, 10000), (102.0, 20000))
    val res = StreamStages.vwap(orders, OrderType.Ask, 30000)
    res shouldEqual (99.0 * 10000 + 100.0 * 20000) / (10000 + 20000)
  }

  "vwap" should "be correct for asks anc c == 1" in {
    val orders = Vector((99.0, 10000), (100.0, 20000), (101.0, 10000), (102.0, 20000))
    val res = StreamStages.vwap(orders, OrderType.Ask, 2)
    res shouldEqual 99.0
  }

  "vwap" should "be correct for empty asks" in {
    val orders = Vector()
    val res = StreamStages.vwap(orders, OrderType.Ask, 2)
    res shouldEqual 0
  }

  "sma" should "be correct for (1.0, 3.2, 5.3, 8.5)" in {
    val vwapMidpoints = Vector(1.0, 3.2, 5.3, 8.5)
    val res = StreamStages.sma(vwapMidpoints)
    res shouldEqual vwapMidpoints.sum / vwapMidpoints.size
  }

  "sma" should "be correct for ()" in {
    val vwapMidpoints = Vector()
    val res = StreamStages.sma(vwapMidpoints)
    res shouldEqual 0
  }

  "sma" should "be correct for (10099.598, 10099.598, 10099.593, 10099.593)" in {
    val vwapMidpoints = Vector(10099.598, 10099.598, 10099.593, 10099.593)
    val res = StreamStages.sma(vwapMidpoints)
    res shouldEqual vwapMidpoints.sum / vwapMidpoints.size
  }

  "ema" should "be correct for (1.0, 3.2, 5.3, 8.5)" in {
    val vwapMidpoints = Vector(1.0, 3.2, 5.3, 8.5)
    val res = StreamStages.ema(vwapMidpoints)
    val a = 2.0 / (vwapMidpoints.size + 1)
    res shouldEqual a * 8.5 + (1.0 - a) * (a * 5.3 + (1.0 - a) * (a * 3.2 + (1.0 - a) * (a * 1.0)))
  }

  "ema" should "be correct for ()" in {
    val vwapMidpoints = Vector()
    val res = StreamStages.ema(vwapMidpoints)
    res shouldEqual 0
  }

  "ema" should "be correct for (10099.598, 10099.598, 10099.593, 10099.593)" in {
    val vwapMidpoints = Vector(10099.598, 10099.598, 10099.593, 10099.593)
    val res = StreamStages.ema(vwapMidpoints)
    val a = 2.0 / (vwapMidpoints.size + 1)
    res shouldEqual a * 10099.593 + (1.0 - a) * (a * 10099.593 + (1.0 - a) * (a * 10099.598 + (1.0 - a) * (a * 10099.598)))
  }
}
