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

package helpers.services

import com.cjwwdev.mongo.responses._
import helpers.other.Fixtures
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import services.RegistrationService

import scala.concurrent.Future

trait MockRegistrationService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockRegistrationService = mock[RegistrationService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockRegistrationService)
  }

  def mockCreateNewUser(created: Boolean): OngoingStubbing[Future[MongoCreateResponse]] = {
    when(mockRegistrationService.createNewUser(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(created) MongoSuccessCreate else MongoFailedCreate))
  }

  def mockCreateNewOrgUser(created: Boolean): OngoingStubbing[Future[MongoCreateResponse]] = {
    when(mockRegistrationService.createNewOrgUser(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(created) MongoSuccessCreate else MongoFailedCreate))
  }
}
