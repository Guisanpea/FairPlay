package es.uma.etsii.fairplay

import cats.Applicative
import es.uma.etsii.fairplay.`match`.{TennisMatch, TennisMatchTable, TennisSet}
import es.uma.etsii.fairplay.player.Player
import io.chrisdavenport.fuuid.circe._
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

package object json {
  implicit def deriveEntityEncoder[F[_] : Applicative, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

  implicit def playerEncoder: Encoder[Player] = deriveEncoder
  implicit def playerDecoder: Decoder[Player] = deriveDecoder

  implicit def matchEncoder: Encoder[TennisMatch] = deriveEncoder
  implicit def matchDecoder: Decoder[TennisMatch] = deriveDecoder

  implicit def matchTEncoder: Encoder[TennisMatchTable] = deriveEncoder
  implicit def matchTDecoder: Decoder[TennisMatchTable] = deriveDecoder

  implicit def scoreEncoder: Encoder[TennisSet] = deriveEncoder
  implicit def scoreDecoder: Decoder[TennisSet] = deriveDecoder
}
