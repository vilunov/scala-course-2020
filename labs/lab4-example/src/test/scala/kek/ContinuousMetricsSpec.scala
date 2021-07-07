package kek

import java.time.LocalDateTime

import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ContinuousMetricsSpec extends AnyFlatSpec with Matchers with OptionValues {

  import ContinuousMetricsSpec._

  private val partialMetrics1 = ContinuousMetrics.continuousMetricsSliding(window)

  "continuousMetricsSliding" should "work correctly with given sequence of InstantMetrics" in {
    partialMetrics1 shouldEqual ContinuousMetrics(defTime, "$", 10, 8.992)
  }
}

object ContinuousMetricsSpec {
  private val defTime = LocalDateTime.of(2020, 2, 9, 19, 34, 20)
  private val window = Seq(
    InstantMetrics(defTime, "$", 12, 5, 5),
    InstantMetrics(defTime, "$", 15, 15, 15),
    InstantMetrics(defTime, "$", 12, 10, 10),
    InstantMetrics(defTime, "$", 12, 10, 10))
}
