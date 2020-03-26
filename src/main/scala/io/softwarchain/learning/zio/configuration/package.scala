package io.softwarchain.learning.zio

import eu.timepit.refined.types.string.NonEmptyString
import zio.{Has, ZIO}

package object configuration {

  type Configuration = Has[ApiConfig]

  final case class AppConfig(api: ApiConfig, aws: AwsConfig)
  final case class ApiConfig(endpoint: String, port: Int)
  final case class AwsConfig(s3: S3Config)
  final case class S3Config(accessKey: NonEmptyString, secretAccessKey: NonEmptyString)

  val apiConfig: ZIO[Has[ApiConfig], Throwable, ApiConfig] = ZIO.access(_.get)
}
