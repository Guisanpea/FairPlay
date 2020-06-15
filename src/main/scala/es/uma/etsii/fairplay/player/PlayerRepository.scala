package es.uma.etsii.fairplay.player

import cats._
import cats.implicits._
import cats.effect.{Bracket, Sync}
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.chrisdavenport.fuuid.FUUID
import io.getquill.{idiom => _, _}
import doobie.implicits._
import es.uma.etsii.fairplay.util.fuuid._
import fs2.Stream
import io.circe.fs2._

final class PlayerRepository[F[_] : Sync : Monad] private(
  transactor: Transactor[F]
) extends DoobieContext.Postgres(Literal) {
  def findAll(): Stream[F, Player] = {
    stream(quote {
      query[Player]
    }, 16)
      .transact(transactor)
  }


  def findPlayer(id: FUUID): F[Option[Player]] = {
    run(quote {
      query[Player].filter(_.id == lift(id))
    })
      .transact(transactor)
      .map(q => q.headOption)
  }

  def createPlayer(): F[Player] = {
    for {
      id <- FUUID.randomFUUID[F]
      player = Player(id)
      _ <- persistPlayer(player)
    } yield (player)
  }

  private def persistPlayer(p: Player): F[_] = {
    run(quote {
      query[Player].insert(lift(p))
    })
      .transact(transactor)
  }
}

object PlayerRepository {
  def make[F[_] : Sync](
    transactor: Transactor[F]
  ): F[PlayerRepository[F]] =
    Sync[F].delay(
      new PlayerRepository[F](transactor)
    )
}
