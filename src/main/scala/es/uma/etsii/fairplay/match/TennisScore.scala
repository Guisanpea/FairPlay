package es.uma.etsii.fairplay.`match`

case class TennisScore(sets: Vector[Set])

case class Set(games: Vector[(Int, Int)])
