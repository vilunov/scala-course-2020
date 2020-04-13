package moneymaker.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class Entry(date: LocalDate, currencies: Map[String, Double]) {

  def toVector(dateFormatter: DateTimeFormatter): Vector[String] = {
    val result = Vector(date.format(dateFormatter))
    result.appendedAll(currencies.toList.map(x => String.format("%s -> %f", x._1, x._2)))
  }
}
