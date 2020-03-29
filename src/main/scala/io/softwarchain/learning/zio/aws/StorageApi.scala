package io.softwarchain.learning.zio.aws

import zio.interop.catz._
import io.circe.generic.auto._
import io.softwarchain.learning.zio.Layers
import io.softwarchain.learning.zio.auth.{Auth, AuthService}
import io.softwarchain.learning.zio.http.ApiError
import zio.interop.catz._
import zio.logging.Logging
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString
import zio.Task
import io.softwarchain.learning.zio.Main.circeJsonEncoder

final case class StorageApi[R <: Storage with Logging]() extends Http4sDsl[Task] {

  def routes: HttpRoutes[Task] = HttpRoutes.of[Task] {
    case req @ GET -> Root / "buckets" =>
      req.headers.get(CaseInsensitiveString("X-Userinfo")).map(_.value) match {
        case None => Ok()
        case Some(userData) => buckets()
          .provideLayer(Layers.storageLayer ++ Layers.loggingLayer ++ AuthService.live(userData).fresh) //Radu: Do I actually need .fresh here?
          .foldM(t => InternalServerError(ApiError(t.getMessage)), listOfBuckets => if(listOfBuckets.nonEmpty) Ok(listOfBuckets) else NoContent())
      }
  }
}
