package es.uma.etsii.fairplay.jokes

import cats.effect.Sync
import cats.implicits._
import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._

class JokesService[F[_]: Sync](C: Client[F]){
  val dsl: Http4sClientDsl[F] = new Http4sClientDsl[F]{}
  import dsl._

  def get: F[Joke] = {
    C.expect[Joke](GET(uri"https://icanhazdadjoke.com/"))
      .adaptError{ case t => JokeError(t)} // Prevent Client Json Decoding Failure Leaking
  }
}

object JokesService {
  def apply[F[_]](implicit ev: JokesService[F]): JokesService[F] = ev

  def apply[F[_]: Sync](client: Client[F]): JokesService[F] = new JokesService(client)
}
