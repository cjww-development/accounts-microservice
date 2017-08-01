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

import config.{EmailInUse, EmailNotInUse, UserNameInUse, UserNameNotInUse}
import helpers.CJWWSpec
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._

import scala.concurrent.Future

class ValidationServiceSpec extends CJWWSpec {

  class Setup {
    val testService = new ValidationService(mockUserAccountRepo, mockOrgAccountRepo)
  }

  "isUserNameInUse" should {
    "return a false" when {
      "the given user name is not in use" in new Setup {
        when(mockUserAccountRepo.verifyUserName(ArgumentMatchers.eq("testUserName")))
          .thenReturn(Future.successful(UserNameNotInUse))

        when(mockOrgAccountRepo.verifyUserName(ArgumentMatchers.eq("testUserName")))
          .thenReturn(Future.successful(UserNameNotInUse))

        val result = await(testService.isUserNameInUse("testUserName"))
        result mustBe false
      }
    }

    "return a true" when {
      "the given user name is already in use" in new Setup {
        when(mockUserAccountRepo.verifyUserName(ArgumentMatchers.eq("testUserName")))
          .thenReturn(Future.successful(UserNameInUse))

        when(mockOrgAccountRepo.verifyUserName(ArgumentMatchers.eq("testUserName")))
          .thenReturn(Future.successful(UserNameNotInUse))

        val result = await(testService.isUserNameInUse("testUserName"))
        result mustBe true
      }
    }
  }

  "isEmailInUse" should {
    "return a continue" when {
      "the given email is not in use" in new Setup {
        when(mockUserAccountRepo.verifyEmail(ArgumentMatchers.eq("test@email.com")))
          .thenReturn(Future.successful(EmailNotInUse))

        when(mockOrgAccountRepo.verifyEmail(ArgumentMatchers.eq("test@email.com")))
          .thenReturn(Future.successful(EmailNotInUse))

        val result = await(testService.isEmailInUse("test@email.com"))
        result mustBe false
      }
    }

    "return a conflict" when {
      "the given user name is already in use" in new Setup {
        when(mockUserAccountRepo.verifyEmail(ArgumentMatchers.eq("test@email.com")))
          .thenReturn(Future.successful(EmailInUse))

        when(mockOrgAccountRepo.verifyEmail(ArgumentMatchers.eq("test@email.com")))
          .thenReturn(Future.successful(EmailNotInUse))

        val result = await(testService.isEmailInUse("test@email.com"))
        result mustBe true
      }
    }
  }
}