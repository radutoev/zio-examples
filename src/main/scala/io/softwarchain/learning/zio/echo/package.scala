package io.softwarchain.learning.zio

import zio.logging.Logging
import zio.{Has, RIO, ZIO}

package object echo {
  object Echo {
    trait Service {
      def echo(message: String): ZIO[Logging, Nothing, Message]
    }
  }

  type Echo = Has[Echo.Service]

  def echo(message: String): ZIO[Echo with Logging, Nothing, Message] = RIO.accessM(_.get.echo(message))

  final case class Message(message: String) extends AnyVal
}
