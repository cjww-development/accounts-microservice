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
import com.cjwwdev.config.ConfigurationLoader
import mocks.SessionBuild
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.ws.ahc.AhcWSClient
import repositories.{OrgAccountRepository, UserAccountRepository, UserFeedRepository}
import services._

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable}

trait CJWWSpec
  extends PlaySpec
    with MockitoSugar
    with BeforeAndAfterEach
    with SessionBuild {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  def await[T](future : Awaitable[T]) : T = Await.result(future, 5.seconds)

  val mockUserAccountRepo     = mock[UserAccountRepository]
  val mockOrgAccountRepo      = mock[OrgAccountRepository]
  val mockUserFeedRepo        = mock[UserFeedRepository]

  val mockAuthConnector       = mock[AuthConnector]

  val mockGetDetailsService   = mock[GetDetailsService]
  val mockAccountService      = mock[AccountService]
  val mockOrgAccountService   = mock[OrgAccountService]
  val mockUserFeedService     = mock[UserFeedService]
  val mockRegService          = mock[RegistrationService]
  val mockValidationService   = mock[ValidationService]

  val mockConfig              = mock[ConfigurationLoader]

  val AUTH_SERVICE_ID         = mockConfig.getApplicationId("auth-service")

//  override def beforeEach(): Unit = {
//    reset(mockUserAccountRepo)
//    reset(mockOrgAccountRepo)
//    reset(mockUserFeedRepo)
//    reset(mockAuthConnector)
//    reset(mockGetDetailsService)
//    reset(mockOrgAccountService)
//    reset(mockUserFeedService)
//    reset(mockRegService)
//  }
}
