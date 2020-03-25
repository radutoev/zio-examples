import sbt._

object Dependencies {

  val Http4sVersion = "0.21.1"
  val ZioVersion = "1.0.0-RC18-2"
  val ZioCatsVersion = "2.0.0.0-RC11"
  val ZioLogging = "0.2.4"
  val TapirVersion = "0.12.23"
  val PureConfigVersion = "0.12.3"
  val DoobieVersion = "0.8.8"
  val H2Version = "1.4.199"
  val RefinedVersion = "0.9.13"
  val NewTypesVersion = "0.4.3"
  val CirceRefinedVersion = "0.13.0"

  lazy val zio = Seq(
    "dev.zio" %% "zio" % ZioVersion,
    "dev.zio" %% "zio-test" % ZioVersion % Test,
    "dev.zio" %% "zio-test-sbt" % ZioVersion % Test
  )
  lazy val zioLogging = Seq(
    "dev.zio" %% "zio-logging" % ZioLogging,
    "dev.zio" %% "zio-logging-slf4j" % ZioLogging
  )
  lazy val zioCats = "dev.zio" %% "zio-interop-cats" % ZioCatsVersion

  lazy val http4s = Seq("org.http4s" %% "http4s-blaze-server" % Http4sVersion,
    "org.http4s" %% "http4s-circe" % Http4sVersion,
    "org.http4s" %% "http4s-dsl" % Http4sVersion)

  lazy val circeModules = Seq(
    "io.circe" %% "circe-refined" % CirceRefinedVersion,
    "io.circe" %% "circe-generic-extras" % CirceRefinedVersion
  )

  lazy val tapir = Seq("com.softwaremill.sttp.tapir" %% "tapir-core" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % TapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % TapirVersion)

  lazy val pureConfig = "com.github.pureconfig" %% "pureconfig" % PureConfigVersion

  lazy val typeHelpers = Seq(
    "eu.timepit" %% "refined" % RefinedVersion,
    "io.estatico" %% "newtype" % NewTypesVersion
  )

  /**
   * @deprecated
   */
  val doobie = Seq(
    "org.tpolecat" %% "doobie-core" % DoobieVersion,
    "org.tpolecat" %% "doobie-h2" % DoobieVersion
  )

  val h2 = "com.h2database" % "h2" % H2Version

  val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.7.26"
}
