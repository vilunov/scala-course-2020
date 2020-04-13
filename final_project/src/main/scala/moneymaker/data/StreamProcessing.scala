package moneymaker.data

import akka.stream.alpakka.csv.scaladsl.CsvFormatting
import akka.stream.scaladsl.{Flow, Sink}
import akka.util.ByteString
import moneymaker.models.Entry

object StreamProcessing {

  val formatter: Flow[Vector[String], ByteString, Any] = CsvFormatting.format()

  val calculateDerivative: Flow[Entry, Entry, Any] = Flow[Entry].sliding(2).map {
    window =>
      val today = window.last
      val yesterday = window.head
      val derivatives = today.currencies.map { case (currency, price) =>
        (currency, price - yesterday.currencies.getOrElse(currency, 0D))
      }
      Entry(today.date, derivatives)
  }

  def calculateDifferenceInCurrenciesIfNeeded(baseCurrency: String,
                                              comparativeCurrency: String,
                                              compare: Boolean): Flow[Entry, Entry, Any] =
    if (compare) Flow[Entry].map {
      entry =>
        Entry(entry.date,
          Map(baseCurrency + " vs. " + comparativeCurrency ->
            (entry.currencies.getOrElse(baseCurrency, 0D) - entry.currencies.getOrElse(comparativeCurrency, 0D))
          )
        )
    } else Flow[Entry]

  def calculateShiftedValues(baseCurrency: String,
                             comparativeCurrency: String,
                             maximumShiftDays: Int): Flow[Entry, Map[Int, Double], Any] =
    Flow[Entry].sliding(2 * maximumShiftDays + 1).map {
      window =>
        calculateDifferenceInsideWindowForValue(
          window.map(x => x.currencies.getOrElse(comparativeCurrency, 0)),
          -maximumShiftDays to maximumShiftDays,
          window(maximumShiftDays).currencies.getOrElse(baseCurrency, 0),
          Map()
        )
    }

  @scala.annotation.tailrec
  private def calculateDifferenceInsideWindowForValue(window: Seq[Double],
                                                      counter: Seq[Int],
                                                      value: Double,
                                                      result: Map[Int, Double]): Map[Int, Double] = {
    if (window.isEmpty || counter.isEmpty) return result
    calculateDifferenceInsideWindowForValue(window.tail, counter.tail, value,
      result + (counter.head -> (value - window.head)))
  }

  val sumShiftedValues: Sink[Map[Int, Double], (Int, Double)] =
    Sink.reduce[Map[Int, Double]]((map1, map2) => map1 ++ map2) //todo reduce all maps to one map by keys
}
