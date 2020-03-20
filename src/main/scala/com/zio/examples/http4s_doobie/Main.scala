package com.zio.examples.http4s_doobie

import cats.effect.ExitCode
import com.zio.examples.http4s_doobie.configuration.ConfigPrd
import com.zio.examples.http4s_doobie.echo.EchoService
import com.zio.examples.http4s_doobie.http.{EchoApi, UserApi}
import com.zio.examples.http4s_doobie.persistence.{UserPersistence, UserPersistenceService}
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.{Console, putStrLn}
import zio.interop.catz._
//import zio.interop.catz.implicits._
import zio.logging.Logging
import zio.logging.slf4j._

object Main extends App {

  type AppEnvironment = Clock with Blocking with UserPersistence with Logging

  type AppTask[A] = RIO[AppEnvironment, A]

  val userPersistence: ZLayer[Any, Throwable, UserPersistence] = (ConfigPrd.live ++ Blocking.live) >>>
      UserPersistenceService.live(platform.executor.asEC)

  val loggingLayer: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make((_, message) => message)

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ZEnv, Throwable, Unit] =
      (for {
        api <- configuration.apiConfig

        httpApp = Router[AppTask](
          "/echo" -> EchoApi(new EchoService()).route,
          "/users" -> UserApi(s"${api.endpoint}/users").route
        ).orNotFound

        server <- ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(api.port, "0.0.0.0")
            .withHttpApp(httpApp)
            .serve
            .compile[AppTask, AppTask, ExitCode]
            .drain
        }
      } yield server).provideSomeLayer[ZEnv](ConfigPrd.live ++ userPersistence ++ loggingLayer)

    program.foldM(
      err => putStrLn(s"Execution failed with: $err") *> IO.succeed(1),
      _ => IO.succeed(0)
    )
  }
}
