package io.softwarchain.learning.zio

import zio.{Has, ZIO}

package object configuration {

  type Configuration = Has[ApiConfig]

  case class AppConfig(api: ApiConfig)
  case class ApiConfig(endpoint: String, port: Int)

  val apiConfig: ZIO[Has[ApiConfig], Throwable, ApiConfig] = ZIO.access(_.get)
}
