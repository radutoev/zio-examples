package io.softwarchain.learning.zio.echo

import io.softwarchain.learning.zio.EchoError
import zio.{ZIO, ZLayer}
import zio.logging.{LogAnnotation, Logging, logInfo, logLocally}

final class EchoService extends Echo.Service {
  override def echo(message: Message): ZIO[Logging, EchoError, Message] =
    logLocally(LogAnnotation.Name("EchoService" :: Nil)) {
      logInfo(s"Echoing $message")
    }.flatMap(_ => ZIO.succeed(message))

  override def fail(message: Message): ZIO[Logging, EchoError, Message] = ZIO.fail(EchoError("Well shit"))
}

object EchoService {
  def live(): ZLayer[Any, Nothing, Echo] = ZLayer.succeed(new EchoService())
}
