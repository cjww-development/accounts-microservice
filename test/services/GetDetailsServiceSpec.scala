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

import helpers.other.AccountEnums
import helpers.services.ServiceSpec

import scala.concurrent.ExecutionContext.Implicits.global

class GetDetailsServiceSpec extends ServiceSpec {

  class Setup {
    val testService = new GetDetailsService {
      override val userAccountRepository = mockUserAccountRepo
    }
  }

  "getBasicDetails" should {
    "return a basic details" when {
      "given a userId" in new Setup {
        mockGetUserBySelector(returned = testUserAccount(AccountEnums.teacher))

        awaitAndAssert(testService.getBasicDetails(generateTestSystemId(USER))) {
          _ mustBe testBasicDetails
        }
      }
    }
  }

  "getEnrolments" should {
    "return an enrolments model" when {
      "given a userId" in new Setup {
        mockGetUserBySelector(returned = testUserAccount(AccountEnums.teacher))

        awaitAndAssert(testService.getEnrolments(generateTestSystemId(USER))) {
          _ mustBe Some(testEnrolments)
        }
      }
    }
  }

  "getSettings" should {
    "return a settings map" when {
      "given a userId" in new Setup {
        mockGetUserBySelector(returned = testUserAccount(AccountEnums.teacher).copy(settings = Some(testSettings)))

        awaitAndAssert(testService.getSettings(generateTestSystemId(USER))) {
          _ mustBe Some(testSettings)
        }
      }
    }

    "return no settings map" when {
      "given a userId" in new Setup {
        mockGetUserBySelector(returned = testUserAccount(AccountEnums.teacher))

        awaitAndAssert(testService.getSettings(generateTestSystemId(USER))) {
          _ mustBe None
        }
      }
    }
  }
}
