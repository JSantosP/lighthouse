import NativePackagerKeys._

packageArchetype.java_application

organization := "lighthouse"

name := "lighthouse-server"

version := "1.0"

scalaVersion := "2.11.6"

resolvers += "spray repo" at "http://repo.spray.io"

libraryDependencies in ThisBuild <++= scalaVersion { (sv: String) =>
  val sprayVersion = "1.3.1"
  val akkaVersion = "2.3.4"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-json" % sprayVersion,
    "org.slf4j" % "slf4j-simple" % "1.7.5",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "junit" % "junit" % "4.10" % "test")
}