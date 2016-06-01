import sbt.Keys._
import sbt._

object Versions {

  val akka = "2.3.12"
  val scalaTestVer = "2.2.2"
  val mockito = "1.10.8"
  val postgresqlVersion = "9.4-1201-jdbc41"
  val slickVersion = "3.1.1"
  val scalaVersion = "2.11.6"
}

object Library {

  import Versions._

  // Core dependencies
  val akkaKit = "com.typesafe.akka" %% "akka-actor" % akka
  val slick = "com.typesafe.slick" %% "slick" % slickVersion
  val scalaLang = "org.scala-lang" % "scala-reflect" % scalaVersion
  val postgresql = "org.postgresql" % "postgresql" % postgresqlVersion
  val hikariCP = "com.zaxxer" % "HikariCP" % "2.4.3"

  val apacheCommons = "org.apache.commons" % "commons-email" % "1.3.3"

  val slickHikari = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion

}

object Dependencies {

  import Library._

  val coreDeps = Seq(akkaKit)

  val slickDeps = Seq(slick, hikariCP, slickHikari,postgresql)

}


object CustomBuild extends Build {

  import Dependencies._

  val common_settings = Defaults.coreDefaultSettings ++
    Seq(
      organization := "com.yadu",
      scalaVersion in ThisBuild := "2.11.7",
      scalacOptions ++= Seq("-unchecked", "-feature", "-deprecation"),
      ivyScala := ivyScala.value map {
        _.copy(overrideScalaVersion = true)
      },

      libraryDependencies := coreDeps ++ slickDeps
    )

  lazy val repository_pattern_slick: Project = Project(id = "slick-repository-pattern",
    base = file("."), settings = common_settings)

}