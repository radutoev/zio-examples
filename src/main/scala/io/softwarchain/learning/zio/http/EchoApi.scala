package io.softwarchain.learning.zio.http

import io.circe.{Decoder, Encoder}
import io.softwarchain.learning.zio.echo._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.dsl.Http4sDsl
import zio._
import org.http4s.circe._
import zio.interop.catz._
import zio.logging.Logging


final case class EchoApi[R <: Echo with Logging]() {
  type EchoTask[A] = RIO[R, A]

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[EchoTask, A] = jsonOf[EchoTask, A]
  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[EchoTask, A] = jsonEncoderOf[EchoTask, A]

  val dsl: Http4sDsl[EchoTask] = Http4sDsl[EchoTask]
  import dsl._

  def route: HttpRoutes[EchoTask] = HttpRoutes.of[EchoTask] {
    case GET  -> Root / message => echo(message).flatMap(echoed => Ok(echoed))
  }
}
