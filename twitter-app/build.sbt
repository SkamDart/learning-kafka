name := "twitter-app"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "1.0.0",
  "com.danielasfregola" %% "twitter4s" % "5.5"
)