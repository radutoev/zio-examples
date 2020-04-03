import Dependencies._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "io.softwarechain.learning"

lazy val root = (project in file("."))
  .settings(
    name := "zio-examples",
    scalacOptions := Seq(
        "-Ymacro-annotations",
        "-language:higherKinds", // Allow higher-kinded types
        "-language:postfixOps"
    ),
    libraryDependencies ++= zio,
    libraryDependencies ++= Seq(zioCats),
    libraryDependencies ++= zioLogging,
    libraryDependencies ++= doobie,
    libraryDependencies ++= Seq(h2),
    libraryDependencies ++= http4s,
    libraryDependencies ++= caliban,
    libraryDependencies ++= circe,
    libraryDependencies ++= aws,
    libraryDependencies ++= Seq(pureConfig),
    libraryDependencies ++= typeHelpers,
    libraryDependencies ++= Seq(slf4j)
  )


// for zio snapshot versions
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
