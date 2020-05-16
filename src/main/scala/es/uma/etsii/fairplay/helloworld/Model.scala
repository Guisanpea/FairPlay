package es.uma.etsii.fairplay.helloworld

import cats.Applicative
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

final case class Name(name: String) extends AnyVal

final case class Greeting(greeting: String) extends AnyVal

object Greeting {

  implicit val greetingEncoder: Encoder[Greeting] = (a: Greeting) =>
    Json.obj(
      ("message", Json.fromString(a.greeting)),
    )

  implicit def greetingEntityEncoder[F[_] : Applicative]: EntityEncoder[F, Greeting] =
    jsonEncoderOf[F, Greeting]
}
