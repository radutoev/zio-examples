package io.softwarchain.learning.zio.aws

import io.circe.generic.auto._
import io.softwarchain.learning.zio.http.ApiError
import zio.interop.catz._
import sttp.tapir.{endpoint, jsonBody}
import sttp.tapir._
import sttp.tapir.json.circe._
import zio.logging.Logging
import org.http4s.{EntityBody, HttpRoutes}
import zio.{Task, URIO, ZIO}
import io.softwarchain.learning.zio.http.RoutesImplicits._
import org.http4s.server.Router
import sttp.tapir.server.http4s.Http4sServerOptions

final case class StorageApi[R <: Storage with Logging]() {
  val listBucketsEndpoint: Endpoint[Unit, ApiError, List[String], Nothing] = endpoint
    .get
    .in("buckets")
    .errorOut(jsonBody[ApiError])
    .out(jsonBody[List[String]])

  implicit class ZioEndpointModified[I, E, O](e: Endpoint[I, E, O, EntityBody[Task]]) {
    def toZioRoutesRModified[ZR](logic: I => ZIO[ZR, E, O])(implicit serverOptions: Http4sServerOptions[Task]): URIO[ZR, HttpRoutes[Task]] = {
      import sttp.tapir.server.http4s._
      URIO.access[ZR](env => e.toRoutes(i => logic(i).provide(env).either))
    }
  }

  val routes: URIO[Storage with Logging, HttpRoutes[Task]] =
    for {
      storageRoutes <- listBucketsEndpoint.toZioRoutesR(_ => buckets().mapError(t => ApiError(t.getMessage)))
    } yield Router("/" -> (storageRoutes))

  val tapirDescription = List(listBucketsEndpoint)
}
