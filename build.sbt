/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.heroku.sbt.HerokuPlugin.autoImport.herokuAppName
import com.typesafe.config.ConfigFactory
import scoverage.ScoverageKeys

import scala.util.{Failure, Success, Try}

val btVersion: String = Try(ConfigFactory.load.getString("version")) match {
  case Success(ver) => ver
  case Failure(_)   => "0.1.0"
}

val appName = "accounts-microservice"

lazy val scoverageSettings = Seq(
  ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;views.* ;utils.*;common.*;.*(AuthService|BuildInfo|Routes).*",
  ScoverageKeys.coverageMinimum          := 80,
  ScoverageKeys.coverageFailOnMinimum    := false,
  ScoverageKeys.coverageHighlighting     := true
)

lazy val root = Project(appName, file("."))
  .enablePlugins(PlayScala)
  .settings(scoverageSettings : _*)
  .configs(IntegrationTest)
  .settings(PlayKeys.playDefaultPort := 8603)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    version                                        :=  btVersion,
    scalaVersion                                   :=  "2.12.8",
    organization                                   :=  "com.cjww-dev.apps",
    libraryDependencies                            ++= AppDependencies(),
    libraryDependencies                            +=  filters,
    resolvers                                      ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      "cjww-dev"       at "http://dl.bintray.com/cjww-development/releases",
      "breadfan"       at "https://dl.bintray.com/breadfan/maven"
    ),
    herokuAppName               in Compile         :=  "cjww-accounts-microservice",
    bintrayOrganization                            :=  Some("cjww-development"),
    bintrayReleaseOnPublish     in ThisBuild       :=  true,
    bintrayRepository                              :=  "releases",
    bintrayOmitLicense                             :=  true,
    Keys.fork                   in IntegrationTest :=  false,
    unmanagedSourceDirectories  in IntegrationTest :=  (baseDirectory in IntegrationTest)(base => Seq(base / "it")).value,
    parallelExecution           in IntegrationTest :=  false
  )
