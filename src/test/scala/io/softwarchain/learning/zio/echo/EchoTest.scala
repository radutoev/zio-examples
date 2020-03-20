package io.softwarchain.learning.zio.echo

import io.softwarchain.learning.zio.echo._
import zio.ZLayer
import zio.clock.Clock
import zio.console.Console
import zio.logging.Logging
import zio.test.Assertion.equalTo
import zio.test.environment.TestEnvironment
import zio.test.{DefaultRunnableSpec, suite, _}

object EchoTest extends DefaultRunnableSpec {
  val echoLayer: ZLayer[Any, Nothing, Echo] = EchoService.live()
  val loggingLayer: ZLayer[Console with Clock, Nothing, Logging] = Logging.console((_, logEntry) => logEntry)

  type EchoTestEnvironment = TestEnvironment with Echo with Logging

  override def spec: Spec[TestEnvironment, TestFailure[Nothing], TestSuccess] = suite("Echo Spec")(
    testM("Received message is returned") {
      checkM(Gen.anyString) { message =>
        for {
          echoed <- echo(message)
        } yield assert(echoed)(equalTo(message))
      }
    }
  ).provideSomeLayer(TestEnvironment.live ++ echoLayer ++ loggingLayer)
}
