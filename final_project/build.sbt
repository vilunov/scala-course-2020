name := "currencies"

version := "0.1"

scalaVersion := "2.13.1"

val akkaVersion = "2.6.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "1.1.2",
  "com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % "2.0.0-RC2",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
)

val circeVersion = "0.13.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)