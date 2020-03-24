package io.softwarchain.learning.zio.http

import org.http4s.{EntityBody, HttpRoutes}
import sttp.tapir.Endpoint
import sttp.tapir.server.http4s.Http4sServerOptions
import zio.{IO, Task, URIO, ZIO}
import zio.interop.catz._

object RoutesImplicits {
  implicit class ZioEndpoint[I, E, O](e: Endpoint[I, E, O, EntityBody[Task]]) {
    def toZioRoutes(logic: I => IO[E, O])(implicit serverOptions: Http4sServerOptions[Task]): HttpRoutes[Task] = {
      import sttp.tapir.server.http4s._
      e.toRoutes(i => logic(i).either)
    }

    def toZioRoutesR[ZR](logic: I => ZIO[ZR, E, O])(implicit serverOptions: Http4sServerOptions[Task]): URIO[ZR, HttpRoutes[Task]] = {
      import sttp.tapir.server.http4s._
      URIO.access[ZR](env => e.toRoutes(i => logic(i).provide(env).either))
    }
  }
}
