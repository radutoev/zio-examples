package io.softwarchain.learning.zio.http

import cats.implicits._
import org.http4s.implicits._
import org.http4s.HttpRoutes
import org.http4s.server.Router
import sttp.tapir._
import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._

import RoutesImplicits._

final case class DummyApi()  {
  val getDummyEndpoint: Endpoint[Unit, String, String, Nothing] = endpoint
    .get
    .in("dummy")
    .errorOut(stringBody)
    .out(stringBody)

  val failingEndpoint: Endpoint[Unit, String, String, Nothing] = endpoint
      .get
      .in("dummy" / "failing")
      .errorOut(stringBody)
      .out(stringBody)

  val routes: HttpRoutes[Task] = Router(
      "/" -> (getDummyEndpoint.toZioRoutes(_ => UIO("dummy")) <+> failingEndpoint.toZioRoutes(_ => ZIO.fail("meh")))
    )

  val tapirDescription = List(getDummyEndpoint, failingEndpoint)
}
