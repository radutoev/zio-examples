package io.softwarchain.learning.zio.auth

import eu.timepit.refined
import io.circe.parser._
import io.softwarchain.learning.zio.auth
import io.softwarchain.learning.zio.user.User
import io.softwarchain.learning.zio.user.User.{UserId, userDecoder, userEmailDecoder}
import io.softwarchain.learning.zio.user.refinements.EmailPredicate
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, suite, _}
import zio.{Task, ZIO}

object AuthTest extends DefaultRunnableSpec {
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("User Info decoding") {
    testM("Decode user info") {
      for {
        decoded <- ZIO.fromEither(auth.decodeUser("eyJhenAiOiJwb3N0bWFuIiwiaWF0IjoxNTg0NTIxNjkwLCJpc3MiOiJodHRwOlwvXC9rZXljbG9hay1ob3N0OjgxODBcL2F1dGhcL3JlYWxtc1wvcGl4ZWxhcnQiLCJlbWFpbCI6InRlc3RAdGVzdC5jb20iLCJnaXZlbl9uYW1lIjoiVGVzdCIsInN1YiI6IjhhNDBlZTQwLTU3YTItNDIxMS05N2RhLWRhZmE1NTkyYWYxYSIsImlkIjoiOGE0MGVlNDAtNTdhMi00MjExLTk3ZGEtZGFmYTU1OTJhZjFhIiwiYXV0aF90aW1lIjowLCJhY3RpdmUiOnRydWUsInVzZXJuYW1lIjoidGVzdCIsIm5iZiI6MCwiZW1haWxfdmVyaWZpZWQiOnRydWUsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJrb25nIjp7InJvbGVzIjpbImtvbmctcm9sZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImF1ZCI6WyJhY2NvdW50Iiwia29uZyJdLCJzZXNzaW9uX3N0YXRlIjoiNjNjOWJiN2MtMDA3NS00OTY5LTk5OTQtNzUxNzVjMDI4OTJiIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6XC9cL2xvY2FsaG9zdDo5OTk5Il0sImFjciI6IjEiLCJjbGllbnRfaWQiOiJwb3N0bWFuIiwiZmFtaWx5X25hbWUiOiJUZXN0IiwiZXhwIjoxNTg0NTIxOTkwLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0IiwianRpIjoiMTVlZTcwZmEtMGVmYS00MmEyLTgwYmYtN2Q1Zjc2ODU0MWE3IiwibmFtZSI6IlRlc3QgVGVzdCIsInR5cCI6IkJlYXJlciJ9"))
      } yield assert(decoded)(equalTo(expectedUser()))
    }
  }

  def expectedUser(): User = (for {
    email <- refined.refineV[EmailPredicate]("test@test.com")
  } yield User(
    id = UserId("8a40ee40-57a2-4211-97da-dafa5592af1a"),
    email = email
  )).getOrElse(null)
}

