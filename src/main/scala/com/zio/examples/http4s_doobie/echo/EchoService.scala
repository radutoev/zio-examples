package com.zio.examples.http4s_doobie.echo

import zio.ZIO
import zio.logging.{LogAnnotation, Logging, logInfo, logLocally}

final class EchoService extends Echo.Service {
  override def echo(message: String): ZIO[Logging, Nothing, String] =
    logLocally(LogAnnotation.Name("EchoService" :: Nil)) {
      logInfo(s"Echoing $message")
    }.flatMap(_ => ZIO.succeed(message))
}
