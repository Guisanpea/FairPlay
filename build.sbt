val Http4sVersion = "1.0.0-M0+388-f965cf9d-SNAPSHOT"
val CirceVersion = "0.13.0"
val Specs2Version = "4.9.3"
val LogbackVersion = "1.2.3"
val TsecVersion = "0.2.0"
val FuuidVersion = "0.3.0"
val doobieVersion = "0.9.0"

lazy val root = (project in file("."))
  .settings(
    organization := "es.uma.etsii",
    name := "fairplay",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.2",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      // Optional for auto-derivation of JSON codecs
      "io.circe" %% "circe-generic" % CirceVersion,
      // Optional for string interpolation to JSON model
      "io.circe" %% "circe-literal" % CirceVersion,
      "io.circe" % "circe-fs2_2.13" % CirceVersion,
      "org.specs2" %% "specs2-core" % Specs2Version % "test",
      "io.chrisdavenport" %% "fuuid" % FuuidVersion,
      "io.chrisdavenport" %% "fuuid-circe" % FuuidVersion, // Circe integration
      "io.chrisdavenport" %% "fuuid-http4s" % FuuidVersion, // Http4s integration
      "io.chrisdavenport" %% "fuuid-doobie" % FuuidVersion, // Doobie integration
      "io.github.jmcardon" %% "tsec-common" % TsecVersion,
      "io.github.jmcardon" %% "tsec-http4s" % TsecVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "org.tpolecat" %% "doobie-core" % doobieVersion,
      "org.tpolecat" %% "doobie-postgres" % doobieVersion,
      "org.tpolecat" %% "doobie-specs2" % doobieVersion,
      "org.tpolecat" %% "doobie-quill" % doobieVersion,
      "org.tpolecat" %% "doobie-hikari" % doobieVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
)

enablePlugins(FlywayPlugin)

flywayUrl := "jdbc:postgresql://localhost:5432/fairplay"
flywayUser := "postgres"
flywayPassword := "example"
flywayLocations += "db/migration"