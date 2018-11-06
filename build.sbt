import java.lang.Integer
import scala.{Predef, Seq, Some, sys}, Predef.assert

import sbt._, Keys._
import sbt.TestFrameworks.Specs2
import scoverage.ScoverageKeys

lazy val buildSettings = Seq(
  organization := "com.slamdata",
  scalaVersion := "2.12.7"
)

/*
lazy val buildSettings = Seq(
  organization := "com.slamdata",
  scalaVersion := "2.12.7",
  scalaOrganization := "org.typelevel",
  initialize :=
    assert(
      Integer.parseInt(sys.props("java.specification.version").split("\\.")(1)) >= 8,
      "Java 8 or above required")
)*/

val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard")

lazy val baseSettings = Seq(
  scalacOptions ++= commonScalacOptions ++ Seq(
    "-target:jvm-1.8",
//    "-Ybackend:GenBCode",
    "-Ydelambdafy:method",
    "-Ypartial-unification",
 //   "-Yliteral-types",
    "-Ywarn-unused-import"),
  scalacOptions in (Test, console) --= Seq(
    "-Yno-imports",
    "-Ywarn-unused-import"),
  scalacOptions in (Compile, doc) -= "-Xfatal-warnings",
  testOptions in (Test) += Tests.Argument(Specs2, "showtimes", "true"),
  // console in Test := console.value,
  //console <<= console in Test,
  wartremoverErrors in (Compile, compile) ++= warts,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    "MarkLogic" at "http://developer.marklogic.com/maven2"),
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),
  ScoverageKeys.coverageHighlighting := true,

  exportJars := true
)

lazy val assemblySettings = Seq(
  test in assembly := {},

  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.last
    case PathList("org", "apache", "hadoop", "yarn", xs @ _*) => MergeStrategy.last
    case PathList("com", "google", "common", "base", xs @ _*) => MergeStrategy.last

    case other => (assemblyMergeStrategy in assembly).value apply other
  }
)

// TODO: sync up wth quasar
val warts = Warts.allBut(
  Wart.Any,
  Wart.AsInstanceOf,
  Wart.ExplicitImplicitTypes, //  - see puffnfresh/wartremover#226
  Wart.ImplicitConversion,    //  - see puffnfresh/wartremover#242
  Wart.IsInstanceOf,
//  Wart.NoNeedForMonad,        //  - see puffnfresh/wartremover#159
  Wart.Nothing,
  Wart.Overloading,
  Wart.Product,               //  _ these two are highly correlated
  Wart.Serializable,          //  /
  Wart.ToString)

lazy val commonSettings = buildSettings ++ baseSettings ++ assemblySettings

lazy val argonautVersion   = "6.2.2"
lazy val monocleVersion    = "1.5.0"
lazy val quasarVersion     = "14.5.7"
lazy val pathyVersion      = "0.2.13"
lazy val refinedVersion    = "0.9.3"
lazy val scalacheckVersion = "1.14.0"
lazy val scalazVersion     = "7.2.22"
lazy val specs2Version     = "4.3.5-78abffa2e-20181150936"
//"3.8.9-scalacheck-1.12.5"

lazy val core = (project in file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "commons-codec"              %  "commons-codec"             % "1.10",
      "org.scalaz"                 %% "scalaz-core"               % scalazVersion,
    //  "org.quasar-analytics"       %% "quasar-foundation-internal" % quasarVersion,
    //  "org.quasar-analytics"       %% "quasar-foundation-internal" % quasarVersion     % "test" classifier "tests",
    //  "org.quasar-analytics"       %% "quasar-connector-internal" % quasarVersion,
     // "org.quasar-analytics"       %% "quasar-connector-internal" % quasarVersion     % "test" classifier "tests",
     // "org.quasar-analytics"       %% "quasar-core-internal"      % quasarVersion,
     // "org.quasar-analytics"       %% "quasar-core-internal"      % quasarVersion     % "test" classifier "tests",
     // "org.quasar-analytics"       %% "quasar-frontend-internal"  % quasarVersion,
     // "org.quasar-analytics"       %% "quasar-frontend-internal"  % quasarVersion     % "test" classifier "tests",
      "com.github.julien-truffaut" %% "monocle-core"              % monocleVersion,
      "com.nimbusds"               %  "oauth2-oidc-sdk"           % "5.13",
      "com.slamdata"               %% "pathy-core"                % pathyVersion,
      "com.slamdata"               %% "pathy-argonaut"            % pathyVersion,
      "com.github.scopt"           %% "scopt"                     % "3.5.0",
      "eu.timepit"                 %% "refined"                   % refinedVersion,
      "eu.timepit"                 %% "refined-scalacheck"        % refinedVersion    % "test",
      "io.argonaut"                %% "argonaut"                  % argonautVersion,
      "io.argonaut"                %% "argonaut-monocle"          % argonautVersion,
      "io.argonaut"                %% "argonaut-scalaz"           % argonautVersion,
      "org.scalacheck"             %% "scalacheck"                % scalacheckVersion % "test",
      "org.specs2"                 %% "specs2-core"               % specs2Version     % "test",
      "org.specs2"                 %% "specs2-scalacheck"         % specs2Version     % "test",
//      "org.scalaz"                 %% "scalaz-scalacheck-binding" % scalazVersion     % "test",
//      "org.typelevel"              %% "scalaz-specs2"             % "0.4.0"           % "test"
  ),
    fork in run := true,
    connectInput in run := true,
    outputStrategy := Some(StdoutOutput),
    buildInfoKeys := Seq[BuildInfoKey](version, ScoverageKeys.coverageEnabled),
    buildInfoPackage := "quasar.advanced.build"
  )

lazy val root = (project in file("."))
  .aggregate(core)
  .dependsOn(core)
  .settings(commonSettings)
