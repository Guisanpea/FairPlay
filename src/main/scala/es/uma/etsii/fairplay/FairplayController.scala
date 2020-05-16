package es.uma.etsii.fairplay

import cats.data.Kleisli
import cats.effect.ConcurrentEffect
import cats.implicits._
import es.uma.etsii.fairplay.helloworld.{HelloWorldController, HelloWorldService}
import es.uma.etsii.fairplay.jokes.{JokesController, JokesService}
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.{Request, Response}

object FairplayController {

  def httpApp[F[_] : ConcurrentEffect](client: Client[F]): Kleisli[F, Request[F], Response[F]] = (
    HelloWorldController.routes(HelloWorldService[F]())
      <+>
      JokesController.routes(JokesService[F](client))
    ).orNotFound
}