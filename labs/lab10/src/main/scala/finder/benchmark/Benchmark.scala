package finder.benchmark


import cats.Monad
import cats.effect.Timer
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.traverse._
import cats.instances.list._

import scala.concurrent.duration.{Duration, NANOSECONDS}

trait Benchmark[F[_]] {
  def benchmark[A](numOfRuns: Int)(process: F[A]): F[BenchmarkResults]
}

object Benchmark {
  implicit def deriveDefaultBenchmark[F[_]: Monad: Timer]: Benchmark[F] = new BenchmarkImpl[F]
}

class BenchmarkImpl[F[_]: Monad: Timer] extends Benchmark[F]{
  override def benchmark[A](numOfRuns: Int)(process: F[A]): F[BenchmarkResults] =
    List.fill(numOfRuns)(singleRun(process)).sequence.map { durations =>
      val totalDuration = durations.foldLeft[Duration](Duration.Zero)(_ + _)
      BenchmarkResults(
        allDurations = durations,
        totalDuration = totalDuration,
        averageDuration = totalDuration / numOfRuns
      )
    }

  // Internal

  private def singleRun[A](process: F[A]): F[Duration] =
    for {
      before <- Timer[F].clock.realTime(NANOSECONDS)
      _ <- process
      after  <- Timer[F].clock.realTime(NANOSECONDS)
    } yield Duration.fromNanos(after - before)
}
