package es.uma.etsii.fairplay.actors

import cats.Monad
import cats.implicits._
import cats.effect.syntax.concurrent._
import cats.effect.{Concurrent, Fiber, Timer}
import cats.effect.concurrent.Deferred
import es.uma.etsii.fairplay.actors.Mailbox.Mailbox
import fs2.concurrent.Queue

object StatelessActor {
  def from[
    F[_] : Concurrent : Monad : Timer,
    Input,
    Output
  ](
    receive: (Input) => F[Output]
  ): F[Mailbox[F, Input, Output]] =
    for {
      queue <- Queue.unbounded[F, (Input, Deferred[F, Output])]
      receiver <- statelessReceiver(receive, queue)
      mailbox = Mailbox(queue, receiver)
    } yield (mailbox)

  private def statelessReceiver[Output, Input, F[_] : Concurrent : Monad](
    receive: (Input) => F[Output],
    queue: Queue[F, (Input, Deferred[F, Output])]
  ): F[Fiber[F, Unit]] =
    (for {
      (input, response) <- queue.dequeue1
      output <- receive(input)
      _ <- response.complete(output)
    } yield ()).foreverM.void.start
}
