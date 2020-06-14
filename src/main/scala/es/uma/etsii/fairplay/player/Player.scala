package es.uma.etsii.fairplay.player

import cats.Applicative
import es.uma.etsii.fairplay.helloworld.Greeting
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.fuuid.circe._
import io.circe.Encoder
import io.circe.generic.semiauto
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

case class Player(id: FUUID)

object Player {
  implicit def playerEncoder: Encoder[Player] =
    deriveEncoder
  implicit def playerEntityEncoder[F[_] : Applicative]: EntityEncoder[F, Player] =
    jsonEncoderOf[F, Player]
}
