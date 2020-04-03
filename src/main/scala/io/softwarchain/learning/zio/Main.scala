package io.softwarchain.learning.zio

import java.util.concurrent.TimeUnit

import caliban.Http4sAdapter
import caliban.wrappers.Wrappers.timeout
import cats.implicits._
import io.circe.{Decoder, Encoder}
import io.softwarchain.learning.zio.configuration.{ApiProd, _}
import io.softwarchain.learning.zio.echo.EchoGql
import io.softwarchain.learning.zio.gql.StorageGql
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import zio._
import zio.duration._
import zio.console.putStrLn
import zio.interop.catz._
import zio.interop.catz.implicits._

import scala.concurrent.duration.FiniteDuration

/**
 * . DynamoDB integration
 * 3. SQS integration
 */
object Main extends App {

  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]): EntityDecoder[Task, A] = jsonOf[Task, A]
  implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]): EntityEncoder[Task, A] = jsonEncoderOf[Task, A]

  type AppTask[A] = RIO[ZEnv, A]

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    val program: ZIO[ZEnv, Throwable, Unit] =
      (for {
        api        <- configuration.apiConfig

        storageGql = StorageGql.gqlApi
        echoGql    = EchoGql.gqlApi

        interpreter <- ((storageGql |+| echoGql) @@ timeout(5 seconds)).interpreter.map(_.provideCustomLayer(
          Layers.loggingLayer ++ Layers.storageLayer ++ Layers.echoLayer
        ))

        httpApp = Router[AppTask](
          "/api/graphql" -> CORS(Http4sAdapter.makeHttpService(interpreter))
        ).orNotFound

        server <- ZIO.runtime[ZEnv].flatMap { implicit rts =>
          BlazeServerBuilder[AppTask]
            .bindHttp(api.port, api.endpoint)
            .withHttpApp(httpApp)
//            .withResponseHeaderTimeout(FiniteDuration.apply(30, TimeUnit.SECONDS))
            .withResponseHeaderTimeout(scala.concurrent.duration.Duration.Inf)
            .withIdleTimeout(FiniteDuration.apply(30, TimeUnit.SECONDS))
            .serve
            .compile
            .drain
        }
      } yield server).provideSomeLayer[ZEnv](ApiProd.live)

    program.foldM(
      err => putStrLn(s"Execution failed with: $err") *> IO.succeed(1),
      _ => IO.succeed(0)
    )
  }
}
