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

import com.cjwwdev.reactivemongo.{MongoFailedUpdate, MongoSuccessUpdate}
import com.cjwwdev.security.encryption.DataSecurity
import config._
import helpers.CJWWSpec
import mocks.AuthBuilder
import models.{Settings, UpdatedPassword, UserProfile}
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class UpdateUserDetailsControllerSpec extends CJWWSpec with ApplicationConfiguration {

  final val now = new DateTime(DateTimeZone.UTC)

  val testProfile =
    UserProfile(
      "testFirst",
      "testLast",
      "testUser",
      "test@email.com",
      None
    )

  val testPasswordUpdate = UpdatedPassword("testOldPassword", "testNewPassword")

  val testSettings =
    Settings(
      Map(
        "displayName" -> "full",
        "displayNameColour" -> "#FFFFFF",
        "displayImageURL" -> "/test/uri"
      )
    )

  class Setup {
    val testController = new UpdateUserDetailsController(mockAccountService, mockAuthConnector)
  }

  "updateProfileInformation" should {
    "return an ok" when {
      "a valid userId is given the profile has been updated" in new Setup {
        when(mockAccountService.updateProfileInformation(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessUpdate))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody[String](DataSecurity.encryptType[UserProfile](testProfile).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateProfileInformation("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem updating the users profile information" in new Setup {
        when(mockAccountService.updateProfileInformation(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedUpdate))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody[String](DataSecurity.encryptType[UserProfile](testProfile).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateProfileInformation("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a forbidden" when {
      "an invalid application is found or none is present in the header" in new Setup {
        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            CONTENT_TYPE -> TEXT
          ).withBody[String](DataSecurity.encryptType[UserProfile](testProfile).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateProfileInformation("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe FORBIDDEN
        }
      }
    }
  }

  "updateUserPassword" should {
    "return an ok" when {
      "the users password has been updated" in new Setup {
        when(mockAccountService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(PasswordUpdated))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody[String](DataSecurity.encryptType[UpdatedPassword](testPasswordUpdate).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateUserPassword("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem updating the users password" in new Setup {
        when(mockAccountService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(PasswordUpdateFailed))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody[String](DataSecurity.encryptType[UpdatedPassword](testPasswordUpdate).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateUserPassword("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a conflict" when {
      "the old password doesn't match what is on record" in new Setup {
        when(mockAccountService.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(InvalidOldPassword))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody[String](DataSecurity.encryptType[UpdatedPassword](testPasswordUpdate).get)


        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateUserPassword("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe CONFLICT
        }
      }
    }

    "return a forbidden" when {
      "an invalid application is found or none is present in the header" in new Setup {
        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            CONTENT_TYPE -> TEXT
          ).withBody[String](DataSecurity.encryptType[UpdatedPassword](testPasswordUpdate).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateUserPassword("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe FORBIDDEN
        }
      }
    }
  }

  "updateUserSettings" should {
    "return an ok" when {
      "the user settings were successfully updated" in new Setup {
        when(mockAccountService.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(UpdatedSettingsSuccess))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody(DataSecurity.encryptType[Settings](testSettings).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateUserSettings("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem updating the users settings" in new Setup {
        when(mockAccountService.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(UpdatedSettingsFailed))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody(DataSecurity.encryptType[Settings](testSettings).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateUserSettings("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a forbidden" when {
      "an invalid application is found or none is present in the header" in new Setup {
        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            CONTENT_TYPE -> TEXT
          ).withBody(DataSecurity.encryptType[Settings](testSettings).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.updateUserSettings("user-766543"), mockAuthConnector, request) {
          result => status(result) mustBe FORBIDDEN
        }
      }
    }
  }
}
