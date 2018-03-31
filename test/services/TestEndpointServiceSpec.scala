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

import com.cjwwdev.mongo.responses.{MongoFailedDelete, MongoSuccessDelete}
import helpers.other.AccountEnums
import helpers.services.ServiceSpec

class TestEndpointServiceSpec extends ServiceSpec {

  class Setup {
    val testService = new TestEndpointService {
      override val userAccountRepository = mockUserAccountRepo
      override val userFeedRepository    = mockUserFeedRepo
      override val orgAccountRepository  = mockOrgAccountRepo
    }
  }

  "tearDownTestUser" should {
    "return a MongoSuccessDelete" in new Setup {
      mockGetUserBySelector(returned = testUserAccount(AccountEnums.basic))
      mockDeleteFeedItems(deleted = true)
      mockDeleteUserAccount(deleted = true)

      awaitAndAssert(testService.tearDownTestUser("testUserName")) {
        _ mustBe MongoSuccessDelete
      }
    }

    "return a MongoFailedDelete" in new Setup {
      mockGetUserBySelector(returned = testUserAccount(AccountEnums.basic))
      mockDeleteFeedItems(deleted = true)
      mockDeleteUserAccount(deleted = false)

      awaitAndAssert(testService.tearDownTestUser("testUserName")) {
        _ mustBe MongoFailedDelete
      }
    }
  }

  "tearDownTestOrgUser" should {
    "return a MongoSuccessDelete" in new Setup {
      mockGetOrgAccount(fetched = true)
      mockDeleteOrgAccount(deleted = true)

      awaitAndAssert(testService.tearDownTestOrgUser("testUserName")) {
        _ mustBe MongoSuccessDelete
      }
    }

    "return a MongoFailedDelete" in new Setup {
      mockGetOrgAccount(fetched = true)
      mockDeleteOrgAccount(deleted = false)

      awaitAndAssert(testService.tearDownTestOrgUser("testUserName")) {
        _ mustBe MongoFailedDelete
      }
    }
  }
}
