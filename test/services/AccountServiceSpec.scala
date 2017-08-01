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

import scala.concurrent.Future

class AccountServiceSpec extends CJWWSpec {

  class Setup {
    val testService = new AccountService(mockUserAccountRepo)
  }

  "updateProfileInformation" should {
    val testData = UserProfile("testFirstName", "testLastName", "testUserName", "test@email.com", None)

    "return a MongoSuccessUpdate" in new Setup {
      when(mockUserAccountRepo.updateAccountData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessUpdate))

      val result = await(testService.updateProfileInformation("testId", testData))
      result mustBe MongoSuccessUpdate
    }

    "return a MongoFailedUpdate" in new Setup {
      when(mockUserAccountRepo.updateAccountData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoFailedUpdate))

      val result = await(testService.updateProfileInformation("testId", testData))
      result mustBe MongoFailedUpdate
    }
  }

  "updatePassword" should {
    val testData = UpdatedPassword("testOldPassword", "testNewPassword")

    "return an InvalidOldPassword" in new Setup {
      when(mockUserAccountRepo.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(false))

      val result = await(testService.updatePassword("testUserId", testData))
      result mustBe InvalidOldPassword
    }

    "return a PasswordUpdated" in new Setup {
      when(mockUserAccountRepo.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(true))

      when(mockUserAccountRepo.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessUpdate))

      val result = await(testService.updatePassword("testUserId", testData))
      result mustBe PasswordUpdated
    }

    "return a PasswordUpdateFailed" in new Setup {
      when(mockUserAccountRepo.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(true))

      when(mockUserAccountRepo.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoFailedUpdate))

      val result = await(testService.updatePassword("testUserId", testData))
      result mustBe PasswordUpdateFailed
    }
  }

  "updateSettings" should {
    val testSettings = Settings("full", "#FFFFFF", "/test/url")

    "return an UpdatedSettingsFailed" in new Setup {
      when(mockUserAccountRepo.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoFailedUpdate))

      val result = await(testService.updateSettings("testId", testSettings))
      result mustBe UpdatedSettingsFailed
    }

    "return an UpdatedSettingsSuccess" in new Setup {
      when(mockUserAccountRepo.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessUpdate))

      val result = await(testService.updateSettings("testId", testSettings))
      result mustBe UpdatedSettingsSuccess
    }
  }
}
