package io.softwarchain.learning.zio.user

import eu.timepit.refined.W
import eu.timepit.refined.api.Refined
import eu.timepit.refined.string.MatchesRegex
import io.circe.Decoder
import io.circe.refined._
import io.circe.generic.extras.ConfiguredJsonCodec
import io.circe.generic.semiauto.deriveDecoder
import io.circe.parser.parse
import io.estatico.newtype.Coercible
import io.estatico.newtype.macros.newtype
import io.softwarchain.learning.zio.user.User.UserId
import io.softwarchain.learning.zio.user.refinements.{Email, EmailPredicate}
import zio.{Task, ZIO}

import scala.util.Try

final case class User(id: UserId, email: Email)

//I can move it this if I need email somewhere else.
object refinements {
  type EmailPredicate = MatchesRegex[W.`"""(.+[@].+)"""`.T] //super dumb, but just to prove the point
  type Email = String Refined EmailPredicate
}

object newTypesCodecs {
  implicit def coercibleDecoder[R, N](implicit ev: Coercible[Decoder[R], Decoder[N]], R: Decoder[R]): Decoder[N] = ev(R)
}

object User {
  import newTypesCodecs._

  implicit val userEmailDecoder: Decoder[EmailPredicate] = deriveDecoder
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]

  @newtype
  @ConfiguredJsonCodec(decodeOnly = true)
  final case class UserId(value: String)
}