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

import com.cjwwdev.mongo._
import helpers.CJWWSpec
import models.UserAccount
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import reactivemongo.bson.BSONDocument

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class RegistrationRepositorySpec extends CJWWSpec {

  val mockConnector = mock[MongoConnector]

  val testUserData = UserAccount.newUser(UserAccount(None, "testFirstName", "testLastName", "testUserName", "test@email.com", "testPass", None, None, None))

  class Setup {
    val testRepo = new RegistrationRepository(mockConnector)
  }

  "Inserting a new user document" should {
    "return a MongoSuccessCreate" in new Setup {
      when(mockConnector.create[UserAccount](ArgumentMatchers.eq(USER_ACCOUNTS), ArgumentMatchers.eq(testUserData))(ArgumentMatchers.eq(UserAccount.format)))
        .thenReturn(Future.successful(MongoSuccessCreate))

      val result = Await.result(testRepo.insertNewUser(testUserData), 5.seconds)
      result mustBe MongoSuccessCreate
    }
  }

  "verifyUserName" should {
    "return a UseNameInUse if the input user name is found in the db" in new Setup {
      val testQuery = BSONDocument("userName" -> "testUserName")

      when(mockConnector.read[UserAccount](ArgumentMatchers.eq(USER_ACCOUNTS), ArgumentMatchers.eq(testQuery))(ArgumentMatchers.eq(UserAccount.format)))
        .thenReturn(Future.successful(MongoSuccessRead(testUserData)))

      val result = Await.result(testRepo.verifyUserName("testUserName"), 5.seconds)
      result mustBe UserNameInUse
    }

    "return a UseNameNotInUse if the input user name is not found in the db" in new Setup {
      val testQuery = BSONDocument("userName" -> "testUserName")

      when(mockConnector.read[UserAccount](ArgumentMatchers.eq(USER_ACCOUNTS), ArgumentMatchers.eq(testQuery))(ArgumentMatchers.eq(UserAccount.format)))
        .thenReturn(Future.successful(MongoFailedRead))

      val result = Await.result(testRepo.verifyUserName("testUserName"), 5.seconds)
      result mustBe UserNameNotInUse
    }
  }

  "verifyEmail" should {
    "return a EmailInUse if the input email address is found in the db" in new Setup {
      val testQuery = BSONDocument("email" -> "test@email.com")

      when(mockConnector.read[UserAccount](ArgumentMatchers.eq(USER_ACCOUNTS), ArgumentMatchers.eq(testQuery))(ArgumentMatchers.eq(UserAccount.format)))
        .thenReturn(Future.successful(MongoSuccessRead(testUserData)))

      val result = Await.result(testRepo.verifyEmail("test@email.com"), 5.seconds)
      result mustBe EmailInUse
    }

    "return a EmailNotInUse if the input email address is not found in the db" in new Setup {
      val testQuery = BSONDocument("email" -> "test@email.com")

      when(mockConnector.read[UserAccount](ArgumentMatchers.eq(USER_ACCOUNTS), ArgumentMatchers.eq(testQuery))(ArgumentMatchers.eq(UserAccount.format)))
        .thenReturn(Future.successful(MongoFailedRead))

      val result = Await.result(testRepo.verifyEmail("test@email.com"), 5.seconds)
      result mustBe EmailNotInUse
    }
  }
}
