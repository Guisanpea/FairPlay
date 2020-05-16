package es.uma.etsii.fairplay.jokes

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object JokesController {

  def routes[F[_]: Sync](service: JokesService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- service.get
          resp <- Ok(joke)
        } yield resp
    }
  }
}
