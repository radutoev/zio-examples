package io.softwarchain.learning.zio

import java.util.concurrent.TimeUnit

import cats.implicits._
import io.softwarchain.learning.zio.aws.{Storage, StorageApi, StorageService}
import io.softwarchain.learning.zio.configuration.{ApiProd, S3Prod}
import io.softwarchain.learning.zio.configuration._
import io.softwarchain.learning.zio.dummy.DummyApi
import io.softwarchain.learning.zio.echo.{Echo, EchoApi, EchoService}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.putStrLn
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.logging.Logging
import zio.logging.slf4j._

import scala.concurrent.duration._

/**
 * 1. Tapir + Swagger
 *       a) better docs
 *       b) status code handling.
 * 2. User info in layer?
 * 3. DynamoDB integration
 * 4. SQS integration
 */
object Main extends App {

  type AppEnvironment = Clock with Blocking
    with S3Configuration
    with Storage
    with Logging
    with Echo

  type AppTask[A] = RIO[AppEnvironment, A]

  val loggingLayer: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make((_, message) => message)
  val echoLayer: ZLayer[Any, Nothing, Echo] = EchoService.live()
  val storageLayer: ZLayer[Any, Throwable, Storage] = (S3Prod.live) >>> StorageService.live()

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ZEnv, Throwable, Unit] =
      (for {
        api        <- configuration.apiConfig

        echoApi       =  EchoApi()
        echoRoutes    <- echoApi.routes
        dummyApi      = DummyApi()
        storageApi    = StorageApi()
        storageRoutes <- storageApi.routes

        yaml       = (echoApi.tapirDescription ++ dummyApi.tapirDescription ++ storageApi.tapirDescription)
                      .toOpenAPI(Info(title = "ZIO Examples", version = "0.1"))
                      .toYaml

        httpApp = (
          echoRoutes <+>
          dummyApi.routes <+>
          storageRoutes <+>
          new SwaggerHttp4s(yaml).routes[Task]
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
      } yield server).provideSomeLayer[ZEnv](ApiProd.live ++ echoLayer ++ loggingLayer ++ storageLayer)

    program.foldM(
      err => putStrLn(s"Execution failed with: $err") *> IO.succeed(1),
      _ => IO.succeed(0)
    )
  }
}
