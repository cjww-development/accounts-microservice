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

import com.cjwwdev.mongo.{MongoFailedCreate, MongoSuccessCreate}
import helpers.CJWWSpec
import models.UserAccount
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import repositories.RegistrationRepository

import scala.concurrent.Future

class RegistrationServiceSpec extends CJWWSpec {

  val mockRepo = mock[RegistrationRepository]

  val user = UserAccount(None, "testFirstName", "testLastName", "testUserName", "test@email.com", "testPass", None, None, None)

  class Setup {
    val testService = new RegistrationService(mockRepo)
  }

  "createNewUser" should {
    "return a MongoSuccessCreate" when {
      "the given user has been inserted into the database" in new Setup {
        when(mockRepo.insertNewUser(ArgumentMatchers.any[UserAccount]()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = await(testService.createNewUser(user))
        result mustBe MongoSuccessCreate
      }
    }

    "return a MongoFailedCreate" when {
      "there were problems inserting the given into the database" in new Setup {
        when(mockRepo.insertNewUser(ArgumentMatchers.any[UserAccount]()))
          .thenReturn(Future.successful(MongoFailedCreate))

        val result = await(testService.createNewUser(user))
        result mustBe MongoFailedCreate
      }
    }
  }
}
