/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package services

import helpers.services.ServiceSpec

class ValidationServiceSpec extends ServiceSpec {

  class Setup {
    val testService = new ValidationService {
      override val userAccountRepository = mockUserAccountRepo
      override val orgAccountRepository  = mockOrgAccountRepo
    }
  }

  "isUserNameInUse" should {
    "return a false" when {
      "the given user name is not in use" in new Setup {
        mockVerifyUserName(inUse = false)

        mockVerifyOrgUserName(inUse = false)

        awaitAndAssert(testService.isUserNameInUse("testUserName")) { res =>
          assert(!res)
        }
      }
    }

    "return a true" when {
      "the given user name is already in use" in new Setup {
        mockVerifyUserName(inUse = true)

        mockVerifyOrgUserName(inUse = false)

        awaitAndAssert(testService.isUserNameInUse("testUserName")) { res =>
          assert(res)
        }
      }
    }
  }

  "isEmailInUse" should {
    "return a continue" when {
      "the given email is not in use" in new Setup {
        mockVerifyEmail(inUse = false)

        mockVerifyOrgEmail(inUse = false)

        awaitAndAssert(testService.isEmailInUse("test@email.com")) { res =>
          assert(!res)
        }
      }
    }

    "return a conflict" when {
      "the given user name is already in use" in new Setup {
        mockVerifyEmail(inUse = true)

        mockVerifyOrgEmail(inUse = false)

        awaitAndAssert(testService.isEmailInUse("test@email.com")) { res =>
          assert(res)
        }
      }
    }
  }
}
