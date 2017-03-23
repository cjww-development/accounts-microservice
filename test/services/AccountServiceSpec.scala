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

import com.cjwwdev.mongo.{MongoFailedUpdate, MongoSuccessUpdate}
import helpers.CJWWSpec
import models.{AccountSettings, UpdatedPassword, UserProfile}
import repositories.AccountDetailsRepository
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class AccountServiceSpec extends CJWWSpec {

  val testProfile =
    UserProfile(
      "testFirst",
      "testLast",
      "testUser",
      "test@email.com",
      None,
      None
    )

  val testUpdatePasswordSet = UpdatedPassword("testOldPassword","testNewPassword")

  val testAccountSettings =
    AccountSettings(
      Map(
        "displayName" -> "full",
        "displayNameColour" -> "#FFFFFF",
        "displayImageURL" -> "/test-uri"
      )
    )

  class Setup {
    val testService = new AccountService(mockAccountDetailsRepo)
  }

  "updateProfileInformation" should {
    "return true" when {
      "given a userId and UserProfile but the profile was not updated" in new Setup {
        when(mockAccountDetailsRepo.updateAccountData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        val result = await(testService.updateProfileInformation("testUserId", testProfile))
        result mustBe true
      }
    }

    "return false" when {
      "given a userId and UserProfile but the profile was not updated" in new Setup {
        when(mockAccountDetailsRepo.updateAccountData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        val result = await(testService.updateProfileInformation("testUserId", testProfile))
        result mustBe false
      }
    }
  }

  "updatePassword" should {
    "return an InvalidOldPassword" when {
      "the provided old password doesn't match" in new Setup {
        when(mockAccountDetailsRepo.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        val result = await(testService.updatePassword("testUserId", testUpdatePasswordSet))
        result mustBe InvalidOldPassword
      }
    }

    "return a PasswordUpdate(true)" when {
      "the old password was found but wasn't updated" in new Setup {
        when(mockAccountDetailsRepo.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockAccountDetailsRepo.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        val result = await(testService.updatePassword("testUserId", testUpdatePasswordSet))
        result mustBe PasswordUpdate(true)
      }
    }

    "return a PasswordUpdate(false)" when {
      "the old password was found and was updated" in new Setup {
        when(mockAccountDetailsRepo.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockAccountDetailsRepo.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        val result = await(testService.updatePassword("testUserId", testUpdatePasswordSet))
        result mustBe PasswordUpdate(false)
      }
    }
  }

  "updateSettings" should {
    "return a UpdatedSettingsFailed" when {
      "there was a problem updating the settings" in new Setup {
        when(mockAccountDetailsRepo.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        val result = await(testService.updateSettings("testUserId", testAccountSettings))
        result mustBe UpdatedSettingsFailed
      }
    }

    "return a UpdatedSettingsSuccess" when {
      "there was a problem updating the settings" in new Setup {
        when(mockAccountDetailsRepo.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        val result = await(testService.updateSettings("testUserId", testAccountSettings))
        result mustBe UpdatedSettingsSuccess
      }
    }
  }
}
