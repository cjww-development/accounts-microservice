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
package repositories

import com.cjwwdev.mongo.{MongoFailedRead, MongoSuccessUpdate, _}
import helpers.CJWWSpec
import models.{AccountSettings, UpdatedPassword, UserAccount, UserProfile}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class AccountDetailsRepositorySpec extends CJWWSpec {

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
      None
    )

  val testProfile =
    UserProfile(
      "testFirst",
      "testLast",
      "testUser",
      "test@email.com",
      None,
      None
    )

  val testAccountSettings =
    AccountSettings(
      Map(
        "displayName" -> "full",
        "displayNameColour" -> "#FFFFFF",
        "displayImageURL" -> "/test-uri"
      )
    )

  val testPasswordSet = UpdatedPassword("testPassword","newTestPassword")

  class Setup {
    val testRepo = new AccountDetailsRepository(mockMongoConnector)
  }

  "updateAccountData" should {
    "return a MongoSuccessUpdate" in new Setup {
      when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessRead(testAccount)))

      when(mockMongoConnector.update(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessUpdate))

      val result = await(testRepo.updateAccountData("testId", testProfile))
      result mustBe MongoSuccessUpdate
    }

    "return a MongoFailedUpdate" in new Setup {
      when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoFailedRead))

      val result = await(testRepo.updateAccountData("testId", testProfile))
      result mustBe MongoFailedUpdate
    }
  }

  "findPassword" should {
    "return true" when {
      "the previous password matches what's on record" in new Setup {
        when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessRead(testAccount)))

        val result = await(testRepo.findPassword("testId", testPasswordSet))
        result mustBe true
      }
    }

    "return false" when {
      "the previous password doesn't match" in new Setup {
        when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedRead))

        val result = await(testRepo.findPassword("testId", testPasswordSet))
        result mustBe false
      }
    }
  }

  "updatePassword" should {
    "return a MongoSuccessUpdate" in new Setup {
      when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessRead(testAccount)))

      when(mockMongoConnector.update(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessUpdate))

      val result = await(testRepo.updatePassword("testId", testPasswordSet))
      result mustBe MongoSuccessUpdate
    }

    "return a a MongoFailedUpdate" in new Setup {
      when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoFailedRead))

      val result = await(testRepo.updatePassword("testId", testPasswordSet))
      result mustBe MongoFailedUpdate
    }
  }

  "updateSettings" should {
    "return a MongoSuccessUpdate" in new Setup {
      when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessRead(testAccount)))

      when(mockMongoConnector.update(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessUpdate))

      val result = await(testRepo.updateSettings("testId", testAccountSettings))
      result mustBe MongoSuccessUpdate
    }

    "return a MongoFailedUpdate" in new Setup {
      when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoFailedRead))

      val result = await(testRepo.updateSettings("testId", testAccountSettings))
      result mustBe MongoFailedUpdate
    }
  }
}
