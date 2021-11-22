
import sbt.ModuleID
import sbt._

object Dependencies {

    lazy val ZioVersion = "1.0.4"
    lazy val PureconfigVersion = "0.12.3"

    lazy val LogbackVersion = "1.2.3"

    lazy val DoobieVersion = "0.8.8"

    lazy val LiquibaseVersion = "3.4.2"

    lazy val PostgresVersion = "42.2.8"
    lazy val CirceVersion = "0.13.0"

    lazy val Http4sVersion = "0.21.7"

    lazy val KindProjectorVersion = "0.10.3"

    lazy val zio: Seq[ModuleID] = Seq(
      "dev.zio" %% "zio" % ZioVersion,
      "dev.zio" %% "zio-test" % ZioVersion,
      "dev.zio" %% "zio-test-sbt" % ZioVersion,
      "dev.zio" %% "zio-macros" % ZioVersion
    )

    lazy val pureconfig: Seq[ModuleID] = Seq(
      "com.github.pureconfig" %% "pureconfig" % PureconfigVersion,
      "com.github.pureconfig" %% "pureconfig-cats-effect" % PureconfigVersion
    )

  // config
  lazy val zioConfig: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio-config" % "1.0.5",
    "dev.zio" %% "zio-config-magnolia" % "1.0.5",
    "dev.zio" %% "zio-config-typesafe" % "1.0.5",
    "dev.zio" %% "zio-interop-cats" % "2.2.0.1"
  )

   // logging
  lazy val logback = "ch.qos.logback"  %  "logback-classic" % LogbackVersion

  lazy val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core"     % DoobieVersion,
    "org.tpolecat" %% "doobie-postgres" % DoobieVersion,
    "org.tpolecat" %% "doobie-hikari"    % DoobieVersion,
    "org.tpolecat" %% "doobie-quill"    % DoobieVersion
  )

  lazy val liquibase = "org.liquibase" % "liquibase-core" % LiquibaseVersion

  lazy val postgres = "org.postgresql" % "postgresql" % PostgresVersion

  // http4s
  lazy val http4sServer: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-dsl"          % Http4sVersion,
    "org.http4s" %% "http4s-circe"        % Http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % Http4sVersion
  )

  // работа с JSON
  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-generic" % CirceVersion,
    "io.circe" %% "circe-generic-extras"% CirceVersion,
    "io.circe" %% "circe-parser" % CirceVersion
  )

  
  lazy val kindProjector = "org.typelevel" %% "kind-projector" % KindProjectorVersion

  lazy val  testContainers = Seq(
      "com.dimafeng"  %% "testcontainers-scala-postgresql" % "0.39.11"  % Test,
      "com.dimafeng"            %% "testcontainers-scala-scalatest"       % "0.39.11"  % Test
      )

}
