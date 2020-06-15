package es.uma.etsii.fairplay.`match`

import cats.implicits._
import cats.effect.Sync
import cats.{Defer, Monad}
import es.uma.etsii.fairplay.json._
import io.chrisdavenport.fuuid.http4s.FUUIDVar
import io.chrisdavenport.fuuid.circe._
import io.chrisdavenport.log4cats.Logger
import io.circe.fs2
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.server.Router
import org.http4s.{HttpRoutes, MediaType}

class MatchController[F[_] : Sync : Logger : Defer : Monad](
  matchRepository: MatchRepository[F]
) extends Http4sDsl[F] {
  private val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root =>
      Ok(matchRepository.findAll()
        .map(_.asJson.noSpaces)
        .through(fs2.stringStreamParser),
        `Content-Type`(MediaType.application.json)
      )

    case GET -> Root / FUUIDVar(id) =>
      matchRepository.findById(id).flatMap(_.fold
      (NotFound())
      (Ok(_))
      )

    case DELETE -> Root / FUUIDVar(id) =>
      matchRepository.deleteById(id) *>
        NoContent()

    case req@POST -> Root => for {
      tennisMatch <- req.asJsonDecode[TennisMatch]
      persistent <- matchRepository.save(tennisMatch)
      result <- Ok(persistent)
    } yield (result)
  }

  val qualifiedRoutes: HttpRoutes[F] = Router(
    "matches" -> routes
  )
}

object MatchController {
  def apply[F[_] : Sync : Defer : Logger : Monad](matchRepository: MatchRepository[F]): MatchController[F] = {
    new MatchController[F](matchRepository)
  }
}
