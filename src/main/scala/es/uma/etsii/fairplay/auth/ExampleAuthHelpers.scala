package es.uma.etsii.fairplay.auth

import java.util.UUID

import cats._
import cats.effect.Sync
import io.chrisdavenport.fuuid.FUUID
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

import io.chrisdavenport.fuuid.circe._

object ExampleAuthHelpers {

  case class User(id: FUUID, age: Int, name: String)

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]

  implicit def userEntityDecoder[F[_] : Sync]: EntityDecoder[F, User] =
    jsonOf

  implicit val userEncoder: Encoder.AsObject[User] = deriveEncoder[User]

  implicit def userEntityEncoder[F[_] : Applicative]: EntityEncoder[F, User] =
    jsonEncoderOf
}
