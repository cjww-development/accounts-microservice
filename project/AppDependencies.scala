// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import sbt._

object AppDependencies {
  def apply(): Seq[ModuleID] = CompileDependencies() ++ UnitTestDependencies() ++ IntegrationTestDependencies()
}

private object CompileDependencies {
  private val dataSecurityVersion  = "2.11.0"
  private val reactiveMongoVersion = "5.4.0"
  private val backendAuthVersion   = "2.17.0"
  private val appUtilsVersion      = "2.10.0"
  private val metricsVersion       = "0.7.0"

  private val compileDependencies: Seq[ModuleID] = Seq(
    "com.cjww-dev.libs" % "data-security_2.11"          % dataSecurityVersion,
    "com.cjww-dev.libs" % "reactive-mongo_2.11"         % reactiveMongoVersion,
    "com.cjww-dev.libs" % "backend-auth_2.11"           % backendAuthVersion,
    "com.cjww-dev.libs" % "application-utilities_2.11"  % appUtilsVersion,
    "com.cjww-dev.libs" % "metrics-reporter_2.11"       % metricsVersion
  )

  def apply(): Seq[ModuleID] = compileDependencies
}

private trait CommonTestDependencies {
  val scope: Configuration
  val testDependencies: Seq[ModuleID]
}

private object UnitTestDependencies extends CommonTestDependencies {
  private val testFrameworkVersion = "0.1.0"

  override val scope: Configuration = Test
  override val testDependencies: Seq[ModuleID] = Seq(
    "com.cjww-dev.libs" % "testing-framework_2.11" % testFrameworkVersion % scope
  )

  def apply(): Seq[ModuleID] = testDependencies
}

private object IntegrationTestDependencies extends CommonTestDependencies {
  private val scalaTestVersion = "2.0.1"

  override val scope: Configuration = IntegrationTest
  override val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestVersion  % scope
  )

  def apply(): Seq[ModuleID] = testDependencies
}
