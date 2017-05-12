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

import helpers.CJWWSpec
import models.{BasicDetails, Enrolments, Settings, UserAccount}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import repositories.UserAccountRepo

import scala.concurrent.Future

class GetDetailsServiceSpec extends CJWWSpec {

  val testAccount =
    UserAccount(
      Some("testId"),
      "testFirst",
      "testLast",
      "testUser",
      "test@email.com",
      "testPassword",
      None,
      None,
      Some(Enrolments(
        Some("testOtherId"),
        Some("testOtherId"),
        Some("testOtherId")
      )),
      Some(Map(
        "displayName" -> "full",
        "displayNameColour" -> "#FFFFFF",
        "displayImageURL" -> "/test-uri"
      ))
    )

  val testAccount2 =
    UserAccount(
      Some("testId"),
      "testFirst",
      "testLast",
      "testUser",
      "test@email.com",
      "testPassword",
      None,
      None,
      Some(Enrolments(
        Some("testOtherId"),
        Some("testOtherId"),
        Some("testOtherId")
      )),
      None
    )

  val testBasicDetails =
    BasicDetails(
      firstName = "testFirst",
      lastName = "testLast",
      userName = "testUser",
      email = "test@email.com",
      metadata = None
    )

  val testEnrolments =
    Enrolments(
      Some("testOtherId"),
      Some("testOtherId"),
      Some("testOtherId")
    )

  val testSettings =
    Settings(
      Some("full"),
      Some("#FFFFFF"),
      Some("/test-uri")
    )

  class Setup {
    val testService = new GetDetailsService(mockUserAccountRepo) {
      override val userAccountStore: UserAccountRepo = mockUserAccountStore
    }
  }

  "getBasicDetails" should {
    "return a basic details" when {
      "given a userId" in new Setup {
        when(mockUserAccountStore.getAccount(ArgumentMatchers.anyString()))
          .thenReturn(Future.successful(testAccount))

        val result = await(testService.getBasicDetails("testId"))
        result mustBe testBasicDetails
      }
    }
  }

  "getEnrolments" should {
    "return an enrolments model" when {
      "given a userId" in new Setup {
        when(mockUserAccountStore.getAccount(ArgumentMatchers.anyString()))
          .thenReturn(Future.successful(testAccount))

        val result = await(testService.getEnrolments("testId"))
        result mustBe Some(testEnrolments)
      }
    }
  }

  "getSettings" should {
    "return a settings map" when {
      "given a userId" in new Setup {
        when(mockUserAccountStore.getAccount(ArgumentMatchers.anyString()))
          .thenReturn(Future.successful(testAccount))

        val result = await(testService.getSettings("testId"))
        result mustBe Some(testSettings)
      }
    }

    "return no settings map" when {
      "given a userId" in new Setup {
        when(mockUserAccountStore.getAccount(ArgumentMatchers.anyString()))
          .thenReturn(Future.successful(testAccount2))

        val result = await(testService.getSettings("testId"))
        result mustBe None
      }
    }
  }
}
