package khinkali

import scala.util.Random

object Utils {
  def randomRange(random: Random, range: TimeRange): Float =
    range.start + (range.end - range.start) * random.nextFloat()
}
