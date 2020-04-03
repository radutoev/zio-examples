package io.softwarchain.learning.zio

import zio.logging.Logging
import zio.{Has, RIO, ZIO}

package object echo {
  object Echo {
    trait Service {
      def echo(message: Message): ZIO[Logging, EchoError, Message]
      def fail(message: Message): ZIO[Logging, EchoError, Message]
    }
  }

  type Echo = Has[Echo.Service]

  def echo(message: Message): ZIO[Echo with Logging, EchoError, Message] = RIO.accessM(_.get.echo(message))
  def fail(message: Message): ZIO[Echo with Logging, EchoError, Message] = RIO.accessM(_.get.fail(message))

  final case class Message(message: String) extends AnyVal
}
