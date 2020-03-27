package io.softwarchain.learning.zio

import io.circe.parser.parse
import io.softwarchain.learning.zio.user.User
import zio.{Has, RIO, UIO, ZIO}

import scala.util.Try

package object auth {
  object Auth {
    trait Service {
      def userInfo(): UIO[User]
    }
  }

  def decodeUser(encodedUserInfo: String): Either[Throwable, User] =
    Try(new String(java.util.Base64.getDecoder.decode(encodedUserInfo)))
      .toEither
      .flatMap(parse)
      .flatMap(json => json.as[User])

  type Auth = Has[Auth.Service]

  def userInfo(): ZIO[Auth, Nothing, User] = RIO.accessM(_.get.userInfo())
}


