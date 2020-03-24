package io.softwarchain.learning.zio.http

import zio._
import zio.interop.catz._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import sttp.tapir._
import sttp.tapir.json.circe._
import io.circe.generic.auto._
import io.softwarchain.learning.zio.echo.{ApiError, Echo, Message}
import io.softwarchain.learning.zio.echo._
import zio.logging.Logging

import RoutesImplicits._

final case class EchoApi[R <: Echo with Logging]()  {
  //TODO How do we take input custom types (maybe refined)
  val getEchoEndpoint: Endpoint[String, ApiError, Message, Nothing] = endpoint
      .get
      .in("echo" / path[String]("message"))
      .errorOut(jsonBody[ApiError])
      .out(jsonBody[Message])

  def routes: URIO[Echo with Logging, HttpRoutes[Task]] = for {
    echoRoutes <- getEchoEndpoint.toZioRoutesR(echo)
  } yield Router("/" -> echoRoutes)
}
