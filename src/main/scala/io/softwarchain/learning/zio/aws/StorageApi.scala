package io.softwarchain.learning.zio.aws

import cats.implicits._
import zio.interop.catz._
import zio.interop.catz.implicits._
import io.circe.generic.auto._
import io.softwarchain.learning.zio.Layers
import io.softwarchain.learning.zio.auth.{Auth, AuthService}
import io.softwarchain.learning.zio.http.ApiError
import zio.interop.catz._
import sttp.tapir.{endpoint, jsonBody}
import sttp.tapir._
import sttp.tapir.json.circe._
import zio.logging.Logging
import org.http4s.HttpRoutes
import zio.{Task, URIO, ZIO}
import io.softwarchain.learning.zio.http.RoutesImplicits._
import org.http4s.server.Router

final case class StorageApi[R <: Storage with Logging]() {
  val listBucketsEndpoint: Endpoint[String, ApiError, List[String], Nothing] = endpoint
    .get
    .in(header[String]("X-Userinfo"))
    .in("buckets")
    .errorOut(jsonBody[ApiError])
    .out(jsonBody[List[String]])

  val listObjectsEndpoint: Endpoint[String, ApiError, List[String], Nothing] = endpoint
    .get
    .in("buckets" / path[String]("bucket") / "objects")
    .errorOut(jsonBody[ApiError])
    .out(jsonBody[List[String]])


  val routes: ZIO[Logging with Storage, Nothing, HttpRoutes[Task]] =
    for {
      bucketsEndpoint <- listBucketsEndpoint.toZioRoutesR(userData => buckets()
        .provideCustomLayer(Layers.storageLayer ++ Layers.loggingLayer ++ AuthService.live(userData).fresh) //Radu: Do I actually need .fresh here?
        .mapError(t => ApiError(t.getMessage))
      )
      //TODO Type signature does not match.
//      objectsEndpoint <- listObjectsEndpoint.toZioRoutesR(bucketName => objects(bucketName).mapError(t => ApiError(t.getMessage)))
    } yield Router("/" -> (bucketsEndpoint))// <+> objectsEndpoint))

  val tapirDescription = List(listBucketsEndpoint, listObjectsEndpoint)
}
