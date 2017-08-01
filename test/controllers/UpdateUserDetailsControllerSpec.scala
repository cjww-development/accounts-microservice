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
package controllers

import java.util.UUID

import com.cjwwdev.reactivemongo.{MongoFailedUpdate, MongoSuccessUpdate}
import com.cjwwdev.security.encryption.{DataSecurity, SHA512}
import config._
import helpers.CJWWSpec
import mocks.AuthBuilder
import models.{Settings, UpdatedPassword, UserProfile}
import play.api.test.Helpers._
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.test.FakeRequest

import scala.concurrent.Future

class UpdateUserDetailsControllerSpec extends CJWWSpec {

  val uuid = UUID.randomUUID

  val testProfile = UserProfile(
    firstName = "testFirstName",
    lastName = "testLastName",
    userName = "tUserName",
    email = "test@email.com",
    settings = None
  )

  val testPasswordUpdate = UpdatedPassword(
    previousPassword = s"${SHA512.encrypt("testOldPassword")}",
    newPassword = s"${SHA512.encrypt("testNewPassword")}"
  )

  val testSettings = Settings(
    displayName = "full",
    displayNameColour = "#FFFFFF",
    displayImageURL = "http://sample-image.com/image.jpg"
  )

  class Setup {
    val testController = new UpdateUserDetailsController(mockAccountService, mockAuthConnector)
  }

  "updateProfileInformation" should {
    "return an Ok" when {
      "the users profile information has been updated" in new Setup {
        val request: FakeRequest[String] = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody[String](
          DataSecurity.encryptType[UserProfile](testProfile)
        )

        when(mockAccountService.updateProfileInformation(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        AuthBuilder.postWithAuthorisedUser(testController.updateProfileInformation(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem updating the users profile information" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(DataSecurity.encryptType[UserProfile](testProfile))

        when(mockAccountService.updateProfileInformation(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        AuthBuilder.postWithAuthorisedUser(testController.updateProfileInformation(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }

  "updateUserPassword" should {
    "return an Ok" when {
      "the users password has been successfully updated" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(DataSecurity.encryptType[UpdatedPassword](testPasswordUpdate))

        when(mockAccountService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(PasswordUpdated))

        AuthBuilder.postWithAuthorisedUser(testController.updateUserPassword(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe OK
        }
      }
    }

    "return a conflict" when {
      "the users old password doesn't match what is on record" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(DataSecurity.encryptType[UpdatedPassword](testPasswordUpdate))

        when(mockAccountService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(InvalidOldPassword))

        AuthBuilder.postWithAuthorisedUser(testController.updateUserPassword(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe CONFLICT
        }
      }
    }

    "return an Internal server error" when {
      "there was a problem updating the users password" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(DataSecurity.encryptType[UpdatedPassword](testPasswordUpdate))

        when(mockAccountService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(PasswordUpdateFailed))

        AuthBuilder.postWithAuthorisedUser(testController.updateUserPassword(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }

  "updateUserSettings" should {
    "return an Ok" when {
      "a users settings have been updated" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(DataSecurity.encryptType[Settings](testSettings))

        when(mockAccountService.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(UpdatedSettingsSuccess))

        AuthBuilder.postWithAuthorisedUser(testController.updateUserSettings(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there a problem updating the users settings" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(DataSecurity.encryptType[Settings](testSettings))

        when(mockAccountService.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(UpdatedSettingsFailed))

        AuthBuilder.postWithAuthorisedUser(testController.updateUserSettings(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
