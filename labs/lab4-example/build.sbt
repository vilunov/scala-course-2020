name := "opium"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.3",
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "1.1.2",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
)
