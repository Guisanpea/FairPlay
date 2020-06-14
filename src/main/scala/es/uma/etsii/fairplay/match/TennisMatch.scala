package es.uma.etsii.fairplay.`match`

import es.uma.etsii.fairplay.player.Player
import io.chrisdavenport.fuuid.FUUID

case class TennisMatch(
                        id: FUUID,
                        player1: Player,
                        player2: Player,
                        winner: Option[Player],
                        score: TennisScore
                      )
