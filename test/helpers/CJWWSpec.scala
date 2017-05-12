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

package helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.cjwwdev.auth.connectors.AuthConnector
import config.ApplicationConfiguration
import mocks.{MongoMocks, SessionBuild}
import org.mockito.Mockito.reset
import org.scalatest.{BeforeAndAfter, TestSuite}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.ws.ahc.AhcWSClient
import repositories._
import services.{AccountService, GetDetailsService, OrgAccountService, UserFeedService}

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable}

trait CJWWSpec
  extends PlaySpec
    with MockitoSugar
    with MongoMocks
    with ApplicationConfiguration
    with BeforeAndAfter
    with OneAppPerSuite
    with TestSuite
    with SessionBuild {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  def await[T](future : Awaitable[T]) : T = Await.result(future, 5.seconds)

  val mockUserAccountRepo = mock[UserAccountRepository]
  val mockUserAccountStore = mock[UserAccountRepo]

  val mockOrgAccountRepo = mock[OrgAccountRepository]
  val mockOrgAccountStore = mock[OrgAccountRepo]

  val mockUserFeedRepo = mock[UserFeedRepository]
  val mockUserFeedStore = mock[UserFeedRepo]

  val mockAuthConnector = mock[AuthConnector]

  val mockAccountService = mock[AccountService]
  val mockGetDetailsService = mock[GetDetailsService]
  val mockUserFeedService = mock[UserFeedService]

  val mockOrgAccountService = mock[OrgAccountService]

  before(
    reset(mockUserAccountRepo),
    reset(mockUserAccountStore),
    reset(mockOrgAccountStore),
    reset(mockOrgAccountStore),
    reset(mockAuthConnector),
    reset(mockAccountService),
    reset(mockGetDetailsService),
    reset(mockUserFeedService),
    reset(mockOrgAccountService)
  )
}
