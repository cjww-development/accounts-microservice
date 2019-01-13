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

import com.cjwwdev.mongo.responses.{MongoFailedUpdate, MongoSuccessUpdate, MongoUpdatedResponse}
import common.{UpdatedPasswordResponse, UpdatedSettingsFailed, UpdatedSettingsResponse, UpdatedSettingsSuccess}
import helpers.other.Fixtures
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import services.AccountService

import scala.concurrent.Future

trait MockAccountService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockAccountService = mock[AccountService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAccountService)
  }

  def mockUpdateProfileInformation(updated: Boolean): OngoingStubbing[Future[MongoUpdatedResponse]] = {
    when(mockAccountService.updateProfileInformation(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(updated) MongoSuccessUpdate else MongoFailedUpdate))
  }

  def mockUpdatePassword(updatedResponse: UpdatedPasswordResponse): OngoingStubbing[Future[UpdatedPasswordResponse]] = {
    when(mockAccountService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(updatedResponse))
  }

  def mockUpdateSettings(updated: Boolean): OngoingStubbing[Future[UpdatedSettingsResponse]] = {
    when(mockAccountService.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(updated) UpdatedSettingsSuccess else UpdatedSettingsFailed))
  }
}
