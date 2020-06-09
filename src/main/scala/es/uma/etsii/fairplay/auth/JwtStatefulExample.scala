package es.uma.etsii.fairplay

import cats.effect.IO
import cats.Id
import org.http4s.{HttpRoutes, HttpService}
import org.http4s.dsl.io._
import tsec.authentication._
import tsec.common.SecureRandomId
import tsec.mac.jca.{HMACSHA256, MacSigningKey}
import es.uma.etsii.fairplay.auth.ExampleAuthHelpers._
import io.chrisdavenport.fuuid.FUUID

import scala.concurrent.duration._

object JwtStatefulExample {
  //Our signing key. Instantiate in a safe way using .generateKey[F]
  val signingKey: MacSigningKey[HMACSHA256] = HMACSHA256.generateKey[Id]

  val jwtStateless: JWTAuthenticator[IO, User, User, HMACSHA256] =
    JWTAuthenticator.pstateless.inBearerToken[IO, User, HMACSHA256](
      expiryDuration = 10.minutes, //Absolute expiration time
      maxIdle        = None,
      signingKey     = signingKey
    )

  val Auth: SecuredRequestHandler[IO, User, User, AugmentedJWT[HMACSHA256, User]] =
    SecuredRequestHandler(jwtStateless)

  /*
  Now from here, if want want to create services, we simply use the following
  (Note: Since the type of the service is HttpService[IO], we can mount it like any other endpoint!):
   */
  val service: HttpRoutes[IO] = Auth.liftService(TSecAuthService {
    //Where user is the case class User above
    case request@GET -> Root / "api" asAuthed user =>
      /*
      Note: The request is of type: SecuredRequest, which carries:
      1. The request
      2. The Authenticator (i.e token)
      3. The identity (i.e in this case, User)
       */
      val r: SecuredRequest[IO, User, AugmentedJWT[HMACSHA256, User]] = request
      Ok(user)
  })

}

