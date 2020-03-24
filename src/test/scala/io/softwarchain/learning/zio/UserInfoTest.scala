package io.softwarchain.learning.zio

import io.circe.parser._
import zio.{Task, ZIO}
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, suite, _}

import scala.util.Try

object UserInfoTest extends DefaultRunnableSpec {
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("User Info decoding") {
    testM("Decode user info") {
      for {
        decoded <- decode("eyJhenAiOiJwb3N0bWFuIiwiaWF0IjoxNTg0NTIxNjkwLCJpc3MiOiJodHRwOlwvXC9rZXljbG9hay1ob3N0OjgxODBcL2F1dGhcL3JlYWxtc1wvcGl4ZWxhcnQiLCJlbWFpbCI6InRlc3RAdGVzdC5jb20iLCJnaXZlbl9uYW1lIjoiVGVzdCIsInN1YiI6IjhhNDBlZTQwLTU3YTItNDIxMS05N2RhLWRhZmE1NTkyYWYxYSIsImlkIjoiOGE0MGVlNDAtNTdhMi00MjExLTk3ZGEtZGFmYTU1OTJhZjFhIiwiYXV0aF90aW1lIjowLCJhY3RpdmUiOnRydWUsInVzZXJuYW1lIjoidGVzdCIsIm5iZiI6MCwiZW1haWxfdmVyaWZpZWQiOnRydWUsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19LCJrb25nIjp7InJvbGVzIjpbImtvbmctcm9sZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImF1ZCI6WyJhY2NvdW50Iiwia29uZyJdLCJzZXNzaW9uX3N0YXRlIjoiNjNjOWJiN2MtMDA3NS00OTY5LTk5OTQtNzUxNzVjMDI4OTJiIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHA6XC9cL2xvY2FsaG9zdDo5OTk5Il0sImFjciI6IjEiLCJjbGllbnRfaWQiOiJwb3N0bWFuIiwiZmFtaWx5X25hbWUiOiJUZXN0IiwiZXhwIjoxNTg0NTIxOTkwLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0IiwianRpIjoiMTVlZTcwZmEtMGVmYS00MmEyLTgwYmYtN2Q1Zjc2ODU0MWE3IiwibmFtZSI6IlRlc3QgVGVzdCIsInR5cCI6IkJlYXJlciJ9")
      } yield assert(decoded)(equalTo("Test"))
    }
  }

  def decode(encodedUserInfo: String): Task[String] = ZIO.fromEither (
    Try(new String(java.util.Base64.getDecoder.decode(encodedUserInfo)))
      .toEither
      .flatMap(parse)
      .flatMap(_.hcursor.get[String]("family_name"))
  )
}
