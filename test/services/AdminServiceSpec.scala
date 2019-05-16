/*
 * Copyright 2019 CJWW Development
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
import play.api.test.FakeRequest
import repositories.{OrgAccountRepository, UserAccountRepository}

import scala.concurrent.ExecutionContext.Implicits.global

class AdminServiceSpec extends ServiceSpec {

  val testService = new AdminService {
    override val userAccountRepository: UserAccountRepository = mockUserAccountRepo
    override val orgAccountRepository: OrgAccountRepository   = mockOrgAccountRepo
  }

  implicit val req = FakeRequest()

  "getIdByUsername" should {
    "return a userId" when {
      "the email address belongs to an individual account" in {
        val userAccount = testUserAccount(AccountEnums.basic)

        mockGetUserBySelector(returned = userAccount)
        mockGetOrgAccount(fetched = false)

        awaitAndAssert(testService.getIdByUsername(userAccount.email)) {
          _ mustBe Some(userAccount.userId)
        }
      }
    }

    "return an orgId" when {
      "the email address belongs to an organisation account" in {
        mockGetUserBySelectorFailed()
        mockGetOrgAccount(fetched = true)

        awaitAndAssert(testService.getIdByUsername(testOrgAccount.orgEmail)) {
          _ mustBe Some(testOrgAccount.orgId)
        }
      }
    }

    "return None" when {
      "the email address is not in use on the service" in {
        val userAccount = testUserAccount(AccountEnums.basic)

        mockGetUserBySelectorFailed()
        mockGetOrgAccount(fetched = false)

        awaitAndAssert(testService.getIdByUsername(userAccount.email)) {
          _ mustBe None
        }
      }
    }
  }
}
