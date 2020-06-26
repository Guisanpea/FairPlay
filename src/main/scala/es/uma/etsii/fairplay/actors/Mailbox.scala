package es.uma.etsii.fairplay.actors

import java.util.concurrent.TimeoutException

import cats.data.Kleisli
import cats.{Monad, MonadError}
import cats.implicits._
import cats.effect.syntax.concurrent._
import cats.effect.{Concurrent, Fiber, Timer}
import cats.effect.concurrent.{Deferred, Ref}
import fs2.concurrent.Queue
import es.uma.etsii.fairplay.actors._

import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration

object Mailbox {

  type Mailbox[F[_], Input, Output] = Input => F[Output]

  case object TimeoutException extends Throwable

  def apply[Output, Input, F[_] : Concurrent : Monad ](
    queue: Queue[F, (Input, Deferred[F, Output])],
    receiver: Fiber[F, Unit],
    timeout: FiniteDuration = 0.seconds
  )(
    implicit timer: Timer[F], F: MonadError[F, Throwable]
  ): Mailbox[F, Input, Output] = (input: Input) => {
    def getTimeout: F[Unit] =
      if (timeout <= 0.seconds) receiver.join
      else timer.sleep(timeout)

    def getTimeoutError: Throwable =
      if (timeout <= 0.seconds) FiberTerminatedException
      else TimeoutException

    for {
      deferredResponse <- Deferred[F, Output]
      _ <- queue.offer1((input, deferredResponse))
      output <- (getTimeout race deferredResponse.get)
        .flatMap {
          case Right(a) => F.pure(a)
          case _ => F.raiseError[Output](getTimeoutError)
        }
    } yield (output)
  }
}
