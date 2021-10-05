import sbt.Keys.libraryDependencies

import Dependencies._


lazy val root = (project in file("."))
  .settings(
    name := "scala-dev-mooc-2021-07",
    version := "0.1",
    scalaVersion := "2.13.3",
    libraryDependencies ++= zio ++ Seq("com.github.pureconfig" %% "pureconfig" % "0.17.0")
  )

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

scalacOptions += "-Ymacro-annotations"