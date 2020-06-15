package es.uma.etsii.fairplay

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    FairplayServer
      .stream[IO]
}