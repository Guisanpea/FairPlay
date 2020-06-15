package es.uma.etsii.fairplay.`match`

import es.uma.etsii.fairplay.player.Player
import io.chrisdavenport.fuuid.FUUID

case class TennisMatch(
  player1: Player,
  player2: Player,
  winner: Option[Player],
  sets: List[TennisSet]
)

case class TennisMatchTable(
  id: FUUID,
  player1: FUUID,
  player2: FUUID,
  winner: Option[FUUID]
)

case class TennisSet(
  player1Games: Int,
  player2Games: Int
)

case class TennisSetTable(
  player1Games: Int,
  player2Games: Int,
  set: Int,
  matchId: FUUID
)

object TennisMatch {
  def matchFromTable(first: TennisMatch, l: List[(TennisMatchTable, Option[Player], Player, Player, TennisSetTable)]): TennisMatch =
    l.foldRight(first) {
      case ((_, _, _, _, ts), m@TennisMatch(_, _, _, sets)) =>
        m.copy(sets = sets :+ TennisSet(ts.player1Games, ts.player2Games))
    }
}
