package io.softwarchain.learning.zio

import java.util.concurrent.TimeUnit

import cats.implicits._
import io.circe.{Decoder, Encoder}
import io.softwarchain.learning.zio.aws.StorageApi
import io.softwarchain.learning.zio.configuration.{ApiProd, _}
import io.softwarchain.learning.zio.echo.{Echo, EchoApi}
import io.softwarchain.learning.zio.Layers._
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.putStrLn
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.logging.Logging

import scala.concurrent.duration._

/**
 * 1. Tapir + Swagger
 *       a) better docs
 *       b) status code handling.
 * 2. DynamoDB integration
 * 3. SQS integration
 */
object Main extends App {

//  type AppEnvironment = Clock with Blocking
//    with Logging
//    with Echo
//
//  type AppTask[A] = RIO[AppEnvironment, A]

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[Task, A] = jsonOf[Task, A]
  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[Task, A] = jsonEncoderOf[Task, A]

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ZEnv, Throwable, Unit] =
      (for {
        api        <- configuration.apiConfig

        echoApi       =  EchoApi()
//        dummyApi      = DummyApi()
        storageApi    = StorageApi()

        httpApp = Router[Task](
          "/echo" -> echoApi.routes,
          "/storage" -> storageApi.routes
        ).orNotFound

        server <- ZIO.runtime[ZEnv].flatMap { implicit rts =>
          BlazeServerBuilder[Task]
            .bindHttp(api.port, api.endpoint)
            .withHttpApp(httpApp)
//            .withResponseHeaderTimeout(FiniteDuration.apply(30, TimeUnit.SECONDS))
            .withResponseHeaderTimeout(Duration.Inf)
            .withIdleTimeout(FiniteDuration.apply(30, TimeUnit.SECONDS))
            .serve
            .compile
            .drain
        }
      } yield server).provideSomeLayer[ZEnv](ApiProd.live ++ echoLayer ++ loggingLayer)

    program.foldM(
      err => putStrLn(s"Execution failed with: $err") *> IO.succeed(1),
      _ => IO.succeed(0)
    )
  }
}
