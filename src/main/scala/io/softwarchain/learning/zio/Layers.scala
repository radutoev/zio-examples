package io.softwarchain.learning.zio

import io.softwarchain.learning.zio.aws.{Storage, StorageService}
import io.softwarchain.learning.zio.configuration.S3Prod
import io.softwarchain.learning.zio.echo.{Echo, EchoService}
import zio.ZLayer
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger

object Layers {

  val loggingLayer: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make((_, message) => message)
  val echoLayer: ZLayer[Any, Nothing, Echo] = EchoService.live()
  val storageLayer: ZLayer[Any, Throwable, Storage] = (S3Prod.live) >>> StorageService.live()
}
