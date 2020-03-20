package com.zio.examples.http4s_doobie

import zio.ZIO
import zio.logging.Logging

package object echo {
  object Echo {
    trait Service {
      def echo(message: String): ZIO[Logging, Nothing, String] //TODO It is aware of Logging, is it ok?
    }
  }
}
