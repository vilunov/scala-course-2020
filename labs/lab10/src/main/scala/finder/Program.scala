package finder

import cats.FlatMap
import finder.benchmark.{Benchmark, BenchmarkReporter}
import finder.domain.Application
import cats.syntax.flatMap._

object Program {
  def run[F[_]](implicit application: Application[F]): F[Unit] =
    application.start

  def runAndBenchmark[F[_]: FlatMap](implicit
                                     application: Application[F],
                                     benchmark: Benchmark[F],
                                     benchmarkReporter: BenchmarkReporter[F]): F[Unit] =
    benchmark.benchmark(10)(application.start) >>= benchmarkReporter.report

}
