package es.uma.etsii.fairplay

import cats.data.Kleisli
import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import es.uma.etsii.fairplay.helloworld.{HelloWorldController, HelloWorldService}
import es.uma.etsii.fairplay.jokes.{JokesController, JokesService}
import es.uma.etsii.fairplay.player.{PlayerController, PlayerRepository}
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.{Request, Response}

object FairplayController {
  private def httpApp[F[_] : ConcurrentEffect](client: Client[F], playerRepository: PlayerRepository[F]): Kleisli[F, Request[F], Response[F]] = (
    HelloWorldController.routes(HelloWorldService[F]())
      <+>
      JokesController.routes(JokesService[F](client))
      <+>
      PlayerController(playerRepository).qualifiedRoutes
    ).orNotFound

  def make[F[_] : Sync : ConcurrentEffect](
    client: Client[F],
    playerRepository: PlayerRepository[F]
  ): F[Kleisli[F, Request[F], Response[F]]] =
    Sync[F].delay(httpApp(client, playerRepository))
}