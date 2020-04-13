package finder.benchmark

import cats.effect.Sync

trait BenchmarkReporter[F[_]] {
  def report(res: BenchmarkResults): F[Unit]
}

object BenchmarkReporter {
  implicit def deriveDefaultBenchmarkReporter[F[_]: Sync]: BenchmarkReporter[F] = new BenchmarkReporterImpl[F]
}

class BenchmarkReporterImpl[F[_]: Sync] extends BenchmarkReporter[F] {
  override def report(res: BenchmarkResults): F[Unit] = {
    Sync[F].delay(println(
      s"""Total duration: ${res.totalDuration}
         |All durations: [${res.allDurations.mkString(", ")}]
         |Average duration: ${res.averageDuration}
         |""".stripMargin))
  }
}
