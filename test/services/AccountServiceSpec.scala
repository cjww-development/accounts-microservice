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
package services

import com.cjwwdev.reactivemongo.{MongoFailedUpdate, MongoSuccessUpdate}
import config._
import helpers.CJWWSpec
import models.{Settings, UpdatedPassword, UserProfile}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import repositories.UserAccountRepo

import scala.concurrent.Future

class AccountServiceSpec extends CJWWSpec {

  val testProfile =
    UserProfile(
      "testFirst",
      "testLast",
      "testUser",
      "test@email.com",
      None
    )

  val testUpdatePasswordSet = UpdatedPassword("testOldPassword","testNewPassword")

  val testAccountSettings =
    Settings(
      displayName = Some("full"),
      displayNameColour = Some("#FFFFFF"),
      displayImageURL  = Some("/test/uri")
    )

  class Setup {
    val testService = new AccountService(mockUserAccountRepo) {
      override val userAccountStore: UserAccountRepo = mockUserAccountStore
    }
  }

  "updateProfileInformation" should {
    "return true" when {
      "given a userId and UserProfile but the profile was not updated" in new Setup {
        when(mockUserAccountStore.updateAccountData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        val result = await(testService.updateProfileInformation("testUserId", testProfile))
        result mustBe MongoFailedUpdate
      }
    }

    "return false" when {
      "given a userId and UserProfile but the profile was not updated" in new Setup {
        when(mockUserAccountStore.updateAccountData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        val result = await(testService.updateProfileInformation("testUserId", testProfile))
        result mustBe MongoSuccessUpdate
      }
    }
  }

  "updatePassword" should {
    "return an InvalidOldPassword" when {
      "the provided old password doesn't match" in new Setup {
        when(mockUserAccountStore.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        val result = await(testService.updatePassword("testUserId", testUpdatePasswordSet))
        result mustBe InvalidOldPassword
      }
    }

    "return a PasswordUpdate(false)" when {
      "the old password was found but wasn't updated" in new Setup {
        when(mockUserAccountStore.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockUserAccountStore.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        val result = await(testService.updatePassword("testUserId", testUpdatePasswordSet))
        result mustBe PasswordUpdateFailed
      }
    }

    "return a PasswordUpdate(true)" when {
      "the old password was found and was updated" in new Setup {
        when(mockUserAccountStore.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockUserAccountStore.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        val result = await(testService.updatePassword("testUserId", testUpdatePasswordSet))
        result mustBe PasswordUpdated
      }
    }
  }

  "updateSettings" should {
    "return a UpdatedSettingsFailed" when {
      "there was a problem updating the settings" in new Setup {
        when(mockUserAccountStore.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        val result = await(testService.updateSettings("testUserId", testAccountSettings))
        result mustBe UpdatedSettingsFailed
      }
    }

    "return a UpdatedSettingsSuccess" when {
      "there was a problem updating the settings" in new Setup {
        when(mockUserAccountStore.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        val result = await(testService.updateSettings("testUserId", testAccountSettings))
        result mustBe UpdatedSettingsSuccess
      }
    }
  }
}
