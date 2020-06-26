package es.uma.etsii.fairplay.actors

import cats.Monad
import cats.implicits._
import cats.effect.syntax.concurrent._
import cats.effect.{Concurrent, ContextShift, Fiber, IO, Timer}
import cats.effect.concurrent.{Deferred, Ref}
import es.uma.etsii.fairplay.actors.Mailbox.Mailbox
import fs2.concurrent.Queue

object StatefulActor {
  def from[
    F[_] : Concurrent : Monad : Timer,
    State,
    Input,
    Output
  ](
    initialState: State,
    receive: (Input, Ref[F, State]) => F[Output]
  ): F[Mailbox[F, Input, Output]] =
    for {
      state <- Ref.of[F, State](initialState)
      queue <- Queue.unbounded[F, (Input, Deferred[F, Output])]
      receiver <- receiver(receive, state, queue)
      mailbox = Mailbox(queue, receiver)
    } yield (mailbox)


  private def receiver[Output, Input, State, F[_] : Concurrent : Monad](
    receive: (Input, Ref[F, State]) => F[Output],
    state: Ref[F, State],
    queue: Queue[F, (Input, Deferred[F, Output])]
  ): F[Fiber[F, Unit]] =
    (for {
      (input, response) <- queue.dequeue1
      output <- receive(input, state)
      _ <- response.complete(output)
    } yield ()).foreverM.void.start
}
