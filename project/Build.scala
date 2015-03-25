import sbt._
import Keys._

object LighthouseBuild extends Build {

  lazy val root = (project in file("."))
    .aggregate(`lighthouse-server`, `lighthouse-client`)
    .settings(
    	run := {
    		(run in `lighthouse-server` in Compile).evaluated
    	}
    )

  lazy val `lighthouse-server` = project

  lazy val `lighthouse-client` = project

}