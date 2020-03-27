package io.softwarchain.learning.zio.echo

import io.circe.generic.auto._
import io.softwarchain.learning.zio.http.ApiError
import io.softwarchain.learning.zio.http.RoutesImplicits._
import io.softwarchain.learning.zio.auth
import org.http4s.HttpRoutes
import org.http4s.server.Router
import sttp.tapir._
import sttp.tapir.json.circe._
import zio._
import zio.interop.catz._
import zio.logging.Logging

final case class EchoApi[R <: Echo with Logging]()  {

  val echoUserEndpoint: Endpoint[String, ApiError, String, Nothing] = endpoint
    .get
    .in("echo" / path[String]("message"))
    .errorOut(jsonBody[ApiError])
    .out(jsonBody[String])

  val routes: URIO[Echo with Logging, HttpRoutes[Task]] =
    for {
      echoRoutes      <- echoUserEndpoint.toZioRoutesR(message => UIO(message))
    } yield Router("/" -> (echoRoutes))

  val tapirDescription = List(echoUserEndpoint)
}

object EchoApi {
  final case class EchoMessage(userInfo: String, message: String)
}

