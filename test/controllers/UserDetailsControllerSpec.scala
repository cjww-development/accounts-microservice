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

import helpers.CJWWSpec
import models.{BasicDetails, Enrolments, Settings}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class UserDetailsControllerSpec extends CJWWSpec {

  val testBasicDetails =
    BasicDetails(
      "testFirstName",
      "testLastName",
      "testUserName",
      "test@email.com",
      None
    )

  val testEnrolments =
    Enrolments(
      Some("testId"),
      Some("testId"),
      Some("testId")
    )

  val testSettings =
    Settings(
      Some("full"),
      Some("#FFFFFF"),
      Some("/test/uri")
    )

  lazy val request = FakeRequest().withHeaders("appId" -> AUTH_ID, CONTENT_TYPE -> TEXT)

  class Setup {
    val testController = new UserDetailsController(mockGetDetailsService)
  }

  "getBasicDetails" should {
    "return an ok" when {
      "the users basic details have been found" in new Setup {
        when(mockGetDetailsService.getBasicDetails(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testBasicDetails)))

        val result = testController.getBasicDetails("testUserId")(request)
        status(result) mustBe OK
      }
    }

    "return a not found" when {
      "no basic details have been found" in new Setup {
        when(mockGetDetailsService.getBasicDetails(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        val result = testController.getBasicDetails("testUserId")(request)
        status(result) mustBe NOT_FOUND
      }
    }

    "return a forbidden" when {
      "no valid appId is present in the header" in new Setup {
        val result = testController.getBasicDetails("testUserId")(FakeRequest())
        status(result) mustBe FORBIDDEN
      }
    }
  }

  "getEnrolments" should {
    "return an ok" when {
      "the users enrolments have been found" in new Setup {
        when(mockGetDetailsService.getEnrolments(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testEnrolments)))

        val result = testController.getEnrolments("testUserId")(request)
        status(result) mustBe OK
      }
    }

    "return a not found" when {
      "no enrolments have been found" in new Setup {
        when(mockGetDetailsService.getEnrolments(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        val result = testController.getEnrolments("testUserId")(request)
        status(result) mustBe NOT_FOUND
      }
    }

    "return a forbidden" when {
      "no valid appId is present in the header" in new Setup {
        val result = testController.getEnrolments("testUserId")(FakeRequest())
        status(result) mustBe FORBIDDEN
      }
    }
  }

  "getSettings" should {
    "return an ok" when {
      "the users settings have been found" in new Setup {
        when(mockGetDetailsService.getSettings(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testSettings)))

        val result = testController.getSettings("testUserId")(request)
        status(result) mustBe OK
      }
    }

    "return a not found" when {
      "no settings have been found" in new Setup {
        when(mockGetDetailsService.getSettings(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        val result = testController.getSettings("testUserId")(request)
        status(result) mustBe NOT_FOUND
      }
    }

    "return a forbidden" when {
      "no valid appId is present in the header" in new Setup {
        val result = testController.getSettings("testUserId")(FakeRequest())
        status(result) mustBe FORBIDDEN
      }
    }
  }
}
