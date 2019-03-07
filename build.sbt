name := "cityBike"

version := "0.1"

scalaVersion := "2.12.8"

val circeVersion = "0.11.1"


libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % "test",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "com.github.kxbmap" %% "configs" % "0.4.4",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe"  %% "circe-generic"  % circeVersion,
  "io.circe"  %% "circe-parser"   % circeVersion,
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
  "org.scalactic" %% "scalactic" % "3.2.0-SNAP10",
  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
)


lazy val global = project
                      .in(file("."))
                      .aggregate(
                        common,
                        clustering
                      )


lazy val common = project.in(file("common"))
 
lazy val clustering = project.in(file("clustering"))
                            .dependsOn(common % "compile->compile;test->test")
