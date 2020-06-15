package es.uma.etsii.fairplay.util

import java.util.UUID

import io.chrisdavenport.fuuid.FUUID
import io.getquill.MappedEncoding
import cats.implicits._

package object fuuid {
  implicit val encodeFuuid: MappedEncoding[FUUID, UUID] = MappedEncoding[FUUID, UUID](f => UUID.fromString(f.show))
  implicit val decodeFuuid: MappedEncoding[UUID, FUUID] = MappedEncoding[UUID, FUUID] { u =>
    FUUID.fromString(u.toString)
      .leftMap(_.toString)
      .getOrElse(throw new IllegalArgumentException(s"Invalid UUID string"))
  }
}
