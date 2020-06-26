package es.uma.etsii.fairplay

import java.nio.file.Paths

import cats.effect.{Blocker, ExitCode, IO, IOApp}

import fs2.io.file
import fs2.text

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    FairplayServer
      .stream[IO]

  def findLine(filename: String, predicate: String => Boolean): IO[Option[String]] = {
    Blocker[IO].use { blocker =>
      file.readAll[IO](Paths.get(filename), blocker, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        .find(predicate)
        .compile
        .fold(Option.empty[String])((_, s) =>
          Option(s))
    }
  }
}