package es.uma.etsii.fairplay

import cats.MonadError
import cats.implicits._

package object actors {

  final case object FiberTerminatedException
    extends RuntimeException(s"The fiber associated with actor has terminated")

  implicit final class FiberTerminatedSyntax[F[_], A](private val fa: F[A]) extends AnyVal {
    def collect[B](pf: PartialFunction[A, B])(implicit F: MonadError[F, Throwable]): F[B] =
      fa.flatMap {
        pf.andThen(F.pure(_))
          .applyOrElse(_, { _: A =>
            F.raiseError[B](FiberTerminatedException)
          })
      }
  }

}
