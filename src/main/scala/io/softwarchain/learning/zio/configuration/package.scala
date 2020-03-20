package io.softwarchain.learning.zio

import zio.{Has, ZIO}

package object configuration {

  type Configuration = Has[ApiConfig] with Has[DbConfig]

  case class AppConfig(api: ApiConfig, dbConfig: DbConfig)
  case class ApiConfig(endpoint: String, port: Int)
  case class DbConfig(
      url: String,
      user: String,
      password: String
  )

  val apiConfig: ZIO[Has[ApiConfig], Throwable, ApiConfig] = ZIO.access(_.get)
  val dbConfig: ZIO[Has[DbConfig], Throwable, DbConfig] = ZIO.access(_.get)
}
