package com.zio.examples.http4s_doobie

import zio.{Has, RIO, ZIO}
import zio.logging.Logging

package object echo {
  object Echo {
    trait Service {
      def echo(message: String): ZIO[Logging, Nothing, String]
    }
  }

  type Echo = Has[Echo.Service]

  def echo(message: String): ZIO[Echo with Logging, Nothing, String] = RIO.accessM(_.get.echo(message))
}
