package es.uma.etsii.fairplay.`match`

import es.uma.etsii.fairplay.player.Player

case class TennisMatch(
                        players: (Player, Player),
                        winner: Player,
                        score: TennisScore
                      )
