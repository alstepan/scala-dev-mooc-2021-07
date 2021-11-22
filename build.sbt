import sbt.Keys.libraryDependencies

import Dependencies._


lazy val root = (project in file("."))
  .settings(
    name := "scala-dev-mooc-2021-07",
    version := "0.1",
    scalaVersion := "2.13.3",
    libraryDependencies ++= zio,
    libraryDependencies ++= pureconfig,
    libraryDependencies ++= zioConfig,
    libraryDependencies ++= doobie,
    libraryDependencies ++= http4sServer,
    libraryDependencies ++= circe,
    libraryDependencies ++= testContainers,
    libraryDependencies ++= Seq(
      kindProjector,
      logback,
      liquibase,
      postgres
    ),
    addCompilerPlugin(Dependencies.kindProjector)
  )

testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))

scalacOptions += "-Ymacro-annotations"