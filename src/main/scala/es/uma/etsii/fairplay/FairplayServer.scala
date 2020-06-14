package es.uma.etsii.fairplay

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import cats.effect.{Bracket, ConcurrentEffect, ContextShift, Timer}
import es.uma.etsii.fairplay.player.PlayerRepository
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object FairplayServer {

  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): F[ExitCode] = {
    AppResources.make.use { res =>
      for {
        playerRepository <- PlayerRepository.make(res.transactor)
        httpApp <- FairplayController.make(res.client, playerRepository)

        // With Middlewares in place
        finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

        exitCode <- BlazeServerBuilder[F](global)
          .bindHttp(8080, "0.0.0.0")
          .withHttpApp(finalHttpApp)
          .serve
          .compile
          .drain
      } yield ExitCode.Success
    }
  }
}