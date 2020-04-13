import sbt._

object Dependencies {

  val all = Seq(
    config.pureconfig,

    https.client,

    cats.core,
    cats.effect,
    tofu.core,
    tofu.logging,
    monix.core,

    logback.classic,
    scalaTest.core % Test
  )

  object config {
    val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.12.3"
  }

  object https {
    val version = "0.21.1"

    val client = "org.http4s" %% "http4s-blaze-client" % version
  }

  object tofu {
    val version = "0.7.3"

    val core = "ru.tinkoff" %% "tofu" % version
    val logging = "ru.tinkoff" %% "tofu-logging" % version
  }

  object cats {
    val effectVersion = "2.1.2"
    val coreVersion = "2.1.1"

    val core   = "org.typelevel" %% "cats-core" % coreVersion
    val effect = "org.typelevel" %% "cats-effect" % effectVersion
  }

  object monix {
    val version = "3.1.0"

    val core = "io.monix" %% "monix" % version
  }

  object logback {
    val version = "1.2.3"

    val classic = "ch.qos.logback" % "logback-classic" % version
  }

  object scalaTest {
    val version = "3.1.1"

    val core = "org.scalatest" %% "scalatest" % version
  }
}
