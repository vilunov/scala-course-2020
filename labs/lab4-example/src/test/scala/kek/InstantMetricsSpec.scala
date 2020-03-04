package kek

import java.time.LocalDateTime

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.util.Random

class InstantMetricsSpec extends AnyFlatSpec with Matchers with OptionValues {

  import InstantMetricsSpec._

  private val result = Entry.fromLine(InstantMetricsSpec.input).value

  "vwap without reach cap" should "produce correct result" in {
    InstantMetrics.vwap(orderedBids, 100) shouldEqual 5.5 / 10
    InstantMetrics.vwap(orderedAsks, 100) shouldEqual 15.5 / 10
  }

  "vwap with reach cap" should "produce correct result" in {
    InstantMetrics.vwap(orderedBids, 2.1) shouldEqual 0.95
    InstantMetrics.vwap(orderedAsks, 2.1) shouldEqual 1.15
  }

  "compute" should "produce correct metrics on shuffled data" in {
    val insM = InstantMetrics.compute(shuffledEntry, 100)
    insM.timestamp shouldEqual shuffledEntry.timestamp
    insM.symbol shouldEqual shuffledEntry.symbol
    insM.vwapAsk shouldEqual 15.5 / 10
    insM.vwapBid shouldEqual 5.5 / 10
    insM.vwapMid shouldEqual (15.5 / 10 + 5.5 / 10) / 2
  }


}

object InstantMetricsSpec {
  private val correct = "2020-02-09 19:34:20,XBTUSD,10112.0,52519,10111.5,301345,10111.0,92817,10110.5,192572,10110.0,128765,10109.5,12415,10109.0,52192,10108.5,374673,10108.0,54287,10107.5,88770,10107.0,260573,10106.5,431409,10106.0,246549,10105.5,99312,10105.0,525907,10104.5,196953,10104.0,511442,10103.5,145059,10103.0,7977,10102.5,131573,10102.0,33399,10101.5,59453,10101.0,54206,10100.5,89967,10100.0,1901198,10099.5,725571,10099.0,52367,10098.5,1863,10098.0,126406,10097.5,1436,10097.0,140522,10096.5,66374,10096.0,92830,10095.5,31524,10095.0,106265,10094.5,528326,10094.0,38185,10093.5,178506,10093.0,164037,10092.5,347790,10092.0,235553,10091.5,146301,10091.0,56262,10090.5,111631,10090.0,170722,10089.5,124646,10089.0,224036,10088.5,126678,10088.0,154786,10087.5,193216"
  private val input = correct.split(',').toList
  private val orderedBids = (for (i <- 1 to 10) yield (i / 10.0, 1)).toVector.reverse
  private val orderedAsks = (for (i <- 11 to 20) yield (i / 10.0, 1)).toVector
  private val shuffledBids = Random.shuffle(orderedBids) // buyer (lower)
  private val shuffledAsks = Random.shuffle(orderedAsks) // seller (higher)

  private val shuffledEntry = Entry(LocalDateTime.of(2020, 2, 9, 19, 34, 20),
    "$", shuffledBids, shuffledAsks)
}
