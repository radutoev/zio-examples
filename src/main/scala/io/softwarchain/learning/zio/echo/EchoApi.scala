package io.softwarchain.learning.zio.echo

import io.circe.generic.auto._
import io.softwarchain.learning.zio.{EchoError, Layers}
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import zio._
import zio.interop.catz._
import zio.logging.Logging
import io.softwarchain.learning.zio.Main.circeJsonEncoder


final case class EchoApi[R <: Echo with Logging]() extends Http4sDsl[Task] {

  def routes: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case GET -> Root / message =>
      val value: ZIO[Echo with Logging, EchoError, Message] = echo(Message(message))
      val withLayers: ZIO[Any, EchoError, Message] = value.provideLayer(Layers.echoLayer ++ Layers.loggingLayer)
      withLayers.foldM(_ => InternalServerError(), echoed => Ok(echoed))
  }
}

object EchoApi {
  final case class EchoMessage(userInfo: String, message: String)
}

