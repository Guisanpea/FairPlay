package es.uma.etsii.fairplay

import cats.implicits._
import cats.effect._
import cats.effect.implicits._
import cats.effect.{Bracket, ConcurrentEffect, ContextShift, Timer}
import es.uma.etsii.fairplay.`match`.MatchRepository
import es.uma.etsii.fairplay.player.PlayerRepository
import fs2.Stream
import io.chrisdavenport.log4cats.{Logger, SelfAwareStructuredLogger}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.{Logger => LoggerMiddleWare}

import scala.concurrent.ExecutionContext.global

object FairplayServer {

  implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  def stream[F[_] : ConcurrentEffect : Timer : ContextShift]: F[ExitCode] = {
    AppResources.make.use { res =>
      for {
        playerRepository <- PlayerRepository.make(res.transactor)
        matchRepository <- MatchRepository.make(res.transactor)
        httpApp <- FairplayController.make(res.client, playerRepository, matchRepository)

        // With Middlewares in place
        finalHttpApp = LoggerMiddleWare.httpApp(logHeaders = true, logBody = true)(httpApp)

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