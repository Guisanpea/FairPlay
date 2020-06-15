package es.uma.etsii.fairplay.player

import cats.implicits._
import cats.effect.Sync
import cats.{Defer, Monad}
import es.uma.etsii.fairplay.json._
import io.chrisdavenport.fuuid.http4s.FUUIDVar
import io.circe.fs2
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.headers.`Content-Type`

class PlayerController[F[_] : Sync: Defer : Monad](
  playerRepository: PlayerRepository[F]
) extends Http4sDsl[F] {
  private val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => Ok(
      playerRepository.findAll()
        .map(_.asJson.noSpaces)
        .through(fs2.stringStreamParser),
      `Content-Type`(MediaType.application.json)
    )

    case GET -> Root / FUUIDVar(id) =>
      playerRepository.findPlayer(id).flatMap(_.fold
      (NotFound())
      (Ok(_))
      )

    case POST -> Root =>
      Ok(playerRepository.createPlayer())
  }

  val qualifiedRoutes: HttpRoutes[F] = Router(
    "players" -> routes
  )
}

object PlayerController {
  def apply[F[_] : Defer : Sync : Monad](playerRepository: PlayerRepository[F]): PlayerController[F] =
    new PlayerController(playerRepository)
}
