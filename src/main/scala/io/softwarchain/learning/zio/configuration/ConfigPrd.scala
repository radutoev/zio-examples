package io.softwarchain.learning.zio.configuration

import pureconfig.loadConfigOrThrow
import zio.{Has, Layer, Task, ZLayer}
import pureconfig.generic.auto._

object ConfigPrd {
  val live: Layer[Throwable, Configuration] = ZLayer.fromEffectMany(
    Task
      .effect(loadConfigOrThrow[AppConfig])
      .map(c => Has(c.api) ++ Has(c.dbConfig)))
}
