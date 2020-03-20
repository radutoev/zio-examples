package com.zio.examples.http4s_doobie.persistence

import com.zio.examples.http4s_doobie.echo._
import zio.logging.Logging
import zio.test._
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, ZSpec, suite}
import zio.test.environment.TestEnvironment

object EchoTest extends DefaultRunnableSpec {
  val echoLayer = EchoService.live()
  val loggingLayer = Logging.console((_, logEntry) => logEntry)

  type EchoTestEnvironment = TestEnvironment with Echo with Logging

  override def spec: Spec[TestEnvironment, TestFailure[Nothing], TestSuccess] = suite("Echo Spec")(
    testM("Received message is returned") {
      for {
        echoed <- echo("message")
      } yield assert(echoed)(equalTo("message"))
    }
  ).provideSomeLayer(TestEnvironment.live ++ echoLayer ++ loggingLayer)
}
