package kek

import java.time.LocalDateTime

final case class Metrics(timestamp: LocalDateTime, symbol: String, meetingPoint: Float) {
  def toVector: Vector[String] = Vector(timestamp.toString, symbol, meetingPoint.toString)
}
