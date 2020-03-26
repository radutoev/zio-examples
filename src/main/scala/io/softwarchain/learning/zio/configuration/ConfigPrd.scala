package io.softwarchain.learning.zio.configuration

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio.{Has, Layer, Task, ZLayer}

object ConfigPrd {
  val live: Layer[Throwable, Configuration] = ZLayer.fromEffectMany(
    Task
      .effect(ConfigSource.default.loadOrThrow[AppConfig])
      .map(c => Has(c.api))) // ++ Has(c.dbConfig)
}
