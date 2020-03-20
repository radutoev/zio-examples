name := "zio-examples"
version := "0.1"
scalaVersion := "2.13.1"

val Http4sVersion = "0.21.1"
val CirceVersion = "0.13.0"
val DoobieVersion = "0.8.8"
val ZIOVersion = "1.0.0-RC18-2"
val PureConfigVersion = "0.12.3"
val H2Version = "1.4.199"
val FlywayVersion = "6.0.0-beta2"

libraryDependencies ++= Seq(
  // ZIO
  "dev.zio" %% "zio" % ZIOVersion,
  "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC12",
  "dev.zio" %% "zio-test" % ZIOVersion % "test",
  "dev.zio" %% "zio-test-sbt" % ZIOVersion % "test",
  // Http4s
  "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s" %% "http4s-circe" % Http4sVersion,
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  // Circe
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-generic-extras" % CirceVersion,
  // Doobie
  "org.tpolecat" %% "doobie-core" % DoobieVersion,
  "org.tpolecat" %% "doobie-h2" % DoobieVersion,
  //pure config
  "com.github.pureconfig" %% "pureconfig" % PureConfigVersion,
  //h2
  "com.h2database" % "h2" % H2Version,
  // log4j
  "dev.zio" %% "zio-logging" % "0.2.4",
  "dev.zio" %% "zio-logging-slf4j" % "0.2.4"
)

// for zio snapshot versions
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
