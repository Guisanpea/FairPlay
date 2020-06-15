package es.uma.etsii.fairplay

import cats.implicits._
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, IO, Resource}
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext.global

final case class AppResources[F[_]](
  client: Client[F],
  transactor: Transactor[F]
)

object AppResources {
  def make[F[_] : ConcurrentEffect: ContextShift]: Resource[F, AppResources[F]] = {
    val client = BlazeClientBuilder[F](global).resource
    val transactor = for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      be <- Blocker[F]
      xa <- HikariTransactor.newHikariTransactor[F](
        "org.postgresql.Driver",
        "jdbc:postgresql://localhost:5432/fairplay",
        "postgres",
        "example",
        ce,
        be
      )
    } yield xa

    (
      client,
      transactor
    ).mapN(AppResources.apply[F])
  }
}
