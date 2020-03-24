package io.softwarchain.learning.zio

import cats.implicits._
import io.softwarchain.learning.zio.configuration.ConfigPrd
import io.softwarchain.learning.zio.echo.{Echo, EchoService}
import io.softwarchain.learning.zio.http.EchoApi
import io.softwarchain.learning.zio.persistence.UserPersistence
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

/**
 * 1. Tapir + Swagger
 *       a) better docs
 *       b) new endpoint
 *       c) reuse implicit class
 *       d) status code handling.
 * 2. S3
 * 3. User info in layer?
 * 4. DynamoDB integration
 * 4. SQS integration
 * 5. Unit Tests / Integration Tests.
 */
object Main extends App {

  type AppEnvironment = Clock with Blocking
    with Logging
    with UserPersistence
    with Echo

  type AppTask[A] = RIO[AppEnvironment, A]

  val loggingLayer: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make((_, message) => message)
  val echoLayer: ZLayer[Any, Nothing, Echo] = EchoService.live()

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ZEnv, Throwable, Unit] =
      (for {
        api <- configuration.apiConfig

        echoApi    =  EchoApi()
        echoRoutes <- echoApi.routes
        yaml       = List(echoApi.getEchoEndpoint).toOpenAPI(Info(title = "ZIO Examples", version = "0.1")).toYaml

        httpApp = (new SwaggerHttp4s(yaml).routes[Task] <+> echoRoutes).orNotFound

        server <- ZIO.runtime[ZEnv].flatMap { implicit rts =>
          BlazeServerBuilder[Task]
            .bindHttp(api.port, api.endpoint)
            .withHttpApp(httpApp)
            .serve
            .compile
            .drain
        }
      } yield server).provideSomeLayer[ZEnv](ConfigPrd.live ++ echoLayer ++ loggingLayer)

    program.foldM(
      err => putStrLn(s"Execution failed with: $err") *> IO.succeed(1),
      _ => IO.succeed(0)
    )
  }
}
