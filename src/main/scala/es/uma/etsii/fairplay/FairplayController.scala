package es.uma.etsii.fairplay

import cats.data.Kleisli
import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import es.uma.etsii.fairplay.`match`.{MatchController, MatchRepository}
import es.uma.etsii.fairplay.helloworld.{HelloWorldController, HelloWorldService}
import es.uma.etsii.fairplay.jokes.{JokesController, JokesService}
import es.uma.etsii.fairplay.player.{PlayerController, PlayerRepository}
import io.chrisdavenport.log4cats.Logger
import org.http4s.client.Client
import org.http4s.implicits._
import org.http4s.{Request, Response}

object FairplayController {
  private def httpApp[F[_] : Logger :ConcurrentEffect](client: Client[F], playerRepository: PlayerRepository[F], matchRepository: MatchRepository[F]): Kleisli[F, Request[F], Response[F]] = (
      PlayerController(playerRepository).qualifiedRoutes
      <+>
      MatchController(matchRepository).qualifiedRoutes
    ).orNotFound

  def make[F[_] : Sync : Logger : ConcurrentEffect](
    client: Client[F],
    playerRepository: PlayerRepository[F],
    matchRepository: MatchRepository[F]
  ): F[Kleisli[F, Request[F], Response[F]]] =
    Sync[F].delay(httpApp(client, playerRepository, matchRepository))
}