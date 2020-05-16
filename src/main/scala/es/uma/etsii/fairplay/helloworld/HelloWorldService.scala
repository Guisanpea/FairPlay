package es.uma.etsii.fairplay.helloworld

import cats.Applicative
import cats.implicits._


class HelloWorldService[F[_]: Applicative]{

  def hello(n: Name): F[Greeting] =
    Greeting("Hello, " + n.name).pure[F]
}

object HelloWorldService {
  implicit def apply[F[_]](implicit ev: HelloWorldService[F]): HelloWorldService[F] = ev

  def apply[F[_]: Applicative](): HelloWorldService[F] = new HelloWorldService()
}