package io.softwarchain.learning.zio.echo

import zio._
import cats.implicits._
import org.http4s.implicits._
import zio.interop.catz._
import zio.interop.catz.implicits._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import sttp.tapir._
import sttp.tapir.json.circe._
import io.circe.generic.auto._
import io.softwarchain.learning.zio.echo.{ApiError, Echo, Message}
import io.softwarchain.learning.zio.echo._
import zio.logging.Logging

import io.softwarchain.learning.zio.http.RoutesImplicits._

final case class EchoApi[R <: Echo with Logging]()  {
  //TODO How do we take input custom types (maybe refined)
  val getEchoEndpoint: Endpoint[(String, String), ApiError, Message, Nothing] = endpoint
    .get
    .in(header[String]("X-Userinfo"))
    .in("echo" / path[String]("message"))
    .errorOut(jsonBody[ApiError])
    .out(jsonBody[Message])

  val getEchoDummyEndpoint: Endpoint[Unit, ApiError, Message, Nothing] = endpoint
    .get
    .in("echo" / "dummy")
    .errorOut(jsonBody[ApiError])
    .out(jsonBody[Message])

  val routes: URIO[Echo with Logging, HttpRoutes[Task]] =
    for {
      echoRoutes      <- getEchoEndpoint.toZioRoutesR(echoMessage => echo(echoMessage._1))
      dummyEchoRoutes <- getEchoDummyEndpoint.toZioRoutesR(_ => echo("test"))
    } yield Router("/" -> (echoRoutes <+> dummyEchoRoutes))

  val tapirDescription = List(getEchoEndpoint, getEchoDummyEndpoint)
}

object EchoApi {
  final case class EchoMessage(userInfo: String, message: String)
}

