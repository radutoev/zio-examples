package com.zio.examples.http4s_doobie.http

import com.zio.examples.http4s_doobie.echo.Echo
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import zio._
import org.http4s.circe._
import zio.interop.catz._
import io.circe.generic.auto._
import zio.logging.Logging


final case class EchoApi[R <: Logging](echoService: Echo.Service) {
  type EchoTask[A] = RIO[R, A]

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[EchoTask, A] = jsonOf[EchoTask, A]
  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[EchoTask, A] = jsonEncoderOf[EchoTask, A]

  val dsl: Http4sDsl[EchoTask] = Http4sDsl[EchoTask]
  import dsl._

  def route: HttpRoutes[EchoTask] = HttpRoutes.of[EchoTask] {
    case GET -> Root => echoService.echo("TEST").flatMap(echo => Ok(echo))
  }
}
