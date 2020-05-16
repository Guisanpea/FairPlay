package es.uma.etsii.fairplay.helloworld

import cats.effect.Sync
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object HelloWorldController {

  def routes[F[_]: Sync](service: HelloWorldService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- service.hello(Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }
}
