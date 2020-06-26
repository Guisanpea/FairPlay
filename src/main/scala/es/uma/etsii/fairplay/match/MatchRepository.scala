package es.uma.etsii.fairplay.`match`

import cats._
import cats.effect.Sync
import cats.implicits._
import doobie.implicits._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import es.uma.etsii.fairplay.player.Player
import es.uma.etsii.fairplay.util.fuuid._
import fs2.{Chunk, Stream}
import io.chrisdavenport.fuuid.FUUID
import io.chrisdavenport.log4cats.Logger
import io.getquill.{idiom => _, _}

final class MatchRepository[F[_] : Sync : Logger : Monad] private(
  transactor: Transactor[F]
) {
  val dctx = new DoobieContext.Postgres(SnakeCase) // Literal naming scheme

  import dctx._

  def findAll(): Stream[F, TennisMatch] =
    stream(findAllMatches)
      .transact(transactor)
      .groupAdjacentBy(_._1.id)
      .map({ case (_, group) => group.toList })
      .map({
        case (_, winner, player1, player2, sets) :: tail =>
          TennisMatch.matchFromTable(
            TennisMatch(player1, player2, winner, List(TennisSet(sets.player1Games, sets.player2Games))),
            tail
          )
      })


  private def findAllMatches =
    quote {
      val tennisMatchQuery = querySchema[TennisMatchTable]("tennis_match")
      for {
        tMatch <- tennisMatchQuery.sortBy(_.id)
        winner <- query[Player].leftJoin(w => tMatch.winner.contains(w.id))
        player1 <- query[Player].join(_.id == tMatch.player1)
        player2 <- query[Player].join(_.id == tMatch.player2)
        sets <- querySchema[TennisSetTable]("tennis_set")
          .join(_.matchId == tMatch.id)
      } yield (tMatch, winner: Option[Player], player1, player2, sets)
    }

  def findById(id: FUUID): F[Option[TennisMatch]] = {
    run(quote {
      for {
        tMatch <- querySchema[TennisMatchTable]("tennis_match")
          .filter(_.id == lift(id))
        winner <- query[Player].leftJoin(w => tMatch.winner.contains(w.id))
        player1 <- query[Player].join(_.id == tMatch.player1)
        player2 <- query[Player].join(_.id == tMatch.player2)
        sets <- querySchema[TennisSetTable]("tennis_set")
          .join(_.matchId == tMatch.id)
      } yield (tMatch, winner, player1, player2, sets)
    })
      .transact(transactor)
      .map {
        case Nil => None
        case (_, winner, player1, player2, sets) :: tail => Some(
          TennisMatch.matchFromTable(
            TennisMatch(player1, player2, winner, List(TennisSet(sets.player1Games, sets.player2Games))),
            tail
          )
        )
      }
  }

  def deleteById(id: FUUID): F[_] =
    run(quote {
      querySchema[TennisMatchTable]("tennis_match")
        .filter(_.id == lift(id)).delete
      querySchema[TennisSetTable]("tennis_set")
        .filter(_.matchId == lift(id)).delete
    })
      .transact(transactor)

  def save(tennisMatch: TennisMatch): F[TennisMatch] = {
    for {
      id <- FUUID.randomFUUID[F]

      TennisMatch(player1, player2, winner, sets) = tennisMatch
      persistent = TennisMatchTable(id, player1.id, player2.id, winner.map(_.id))

      _ <- persistMatch(persistent)
      _ <- persistSets(sets, id)
    } yield (tennisMatch)
  }

  private def persistMatch(t: TennisMatchTable): F[_] = {
    run(quote {
      querySchema[TennisMatchTable]("tennis_match")
        .insert(lift(t))
    })
      .transact(transactor)
  }

  private def persistSets(sets: List[TennisSet], id: FUUID): F[_] = {
    val tennisSets = sets.zipWithIndex.map {
      case (s, i) => TennisSetTable(s.player1Games, s.player2Games, i, id)
    }
    run(quote {
      liftQuery(tennisSets).foreach(s =>
        querySchema[TennisSetTable]("tennis_set").insert(s)
      )})
      .transact(transactor)
  }
}

object MatchRepository {
  def make[F[_] : Sync : Monad : Logger](transactor: Transactor[F]): F[MatchRepository[F]] =
    Sync[F].delay(
      new MatchRepository[F](transactor)
    )
}
