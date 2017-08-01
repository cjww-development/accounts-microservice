import com.typesafe.config.ConfigFactory
import scoverage.ScoverageKeys

import scala.util.{Failure, Success, Try}

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

name         := """accounts-microservice"""
version      := btVersion
scalaVersion := "2.11.11"
organization := "com.cjww-dev.backends"

lazy val playSettings : Seq[Setting[_]] = Seq.empty

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;views.* ;utils.*;config.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum          := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(playSettings ++ scoverageSettings : _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base => Seq(base / "it")),
    parallelExecution in IntegrationTest := false)

PlayKeys.devSettings := Seq("play.server.http.port" -> "8603")

val cjwwDep: Seq[ModuleID] = Seq(
  "com.cjww-dev.libs" % "data-security_2.11"          % "2.5.0",
  "com.cjww-dev.libs" % "reactive-mongo_2.11"         % "3.1.0",
  "com.cjww-dev.libs" % "backend-auth_2.11"           % "2.6.0",
  "com.cjww-dev.libs" % "application-utilities_2.11"  % "1.2.0"
)

val testDep: Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play"  % "2.0.0" % Test,
  "org.mockito"             % "mockito-core"        % "2.8.47" % Test
)

libraryDependencies ++= cjwwDep
libraryDependencies ++= testDep

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers += "cjww-dev" at "http://dl.bintray.com/cjww-development/releases"

herokuAppName in Compile := "cjww-accounts-microservice"

bintrayOrganization                  := Some("cjww-development")
bintrayReleaseOnPublish in ThisBuild := true
bintrayRepository                    := "releases"
bintrayOmitLicense                   := true
