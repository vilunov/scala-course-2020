package finder.benchmark

import scala.concurrent.duration.Duration

case class BenchmarkResults(totalDuration: Duration,
                            allDurations: List[Duration],
                            averageDuration: Duration)
