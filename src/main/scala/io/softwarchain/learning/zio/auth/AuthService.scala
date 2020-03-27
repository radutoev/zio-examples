package io.softwarchain.learning.zio.auth

import io.softwarchain.learning.zio.auth
import io.softwarchain.learning.zio.user.User
import zio.{UIO, ZLayer}

final class AuthService(user: User) extends Auth.Service {
  override def userInfo(): UIO[User] = UIO(user)
}

object AuthService {
  //TODO can use refined here.
  def live(encodedUserData: String): ZLayer[Any, Throwable, Auth] =
    auth.decodeUser(encodedUserData).fold(
      failed => ZLayer.fail(failed),
      user => ZLayer.succeed(new AuthService(user))
    )
}
