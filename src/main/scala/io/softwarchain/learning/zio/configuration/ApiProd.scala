package io.softwarchain.learning.zio.configuration

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.{Has, Layer, Task, ZLayer}

object Config {
  lazy val configTask: Task[AppConfig] = Task.effect(ConfigSource.default.loadOrThrow[AppConfig])
}

object ApiProd {
  val live: Layer[Throwable, ApiConfiguration] = ZLayer.fromEffectMany(
    Config.configTask.map(c => Has(c.api)))
}

object S3Prod {
  val live: Layer[Throwable, S3Configuration] = ZLayer.fromEffectMany(
    Config.configTask.map(c => Has(c.aws.s3))
  )
}
