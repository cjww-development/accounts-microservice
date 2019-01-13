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

import com.cjwwdev.mongo.responses.MongoSuccessCreate
import common.FailedToCreateException
import helpers.other.AccountEnums
import helpers.services.ServiceSpec

import scala.concurrent.ExecutionContext.Implicits.global

class RegistrationServiceSpec extends ServiceSpec {

  class Setup {
    val testService = new RegistrationService {
      override val userAccountRepository = mockUserAccountRepo
      override val orgAccountRepository  = mockOrgAccountRepo
    }
  }

  "createNewUser" should {
    "return a MongoSuccessCreate" when {
      "the given user has been inserted into the database" in new Setup {
        mockInsertNewUser(inserted = true)

        awaitAndAssert(testService.createNewUser(testUserAccount(AccountEnums.basic))) {
          _ mustBe MongoSuccessCreate
        }
      }
    }

    "return a MongoFailedCreate" when {
      "there were problems inserting the given into the database" in new Setup {
        mockInsertNewUser(inserted = false)

        awaitAndIntercept[FailedToCreateException](testService.createNewUser(testUserAccount(AccountEnums.basic)))
      }
    }
  }

  "createNewOrgUser" should {
    "return a MongoSuccessCreate" when {
      "the given org user has been inserted into the database" in new Setup {
        mockInsertNewOrgUser(inserted = true)

        awaitAndAssert(testService.createNewOrgUser(testOrgAccount)) {
          _ mustBe MongoSuccessCreate
        }
      }
    }

    "return a MongoFailedCreate" when {
      "there were problems inserting the given org user into the database" in new Setup {
        mockInsertNewOrgUser(inserted = false)

        awaitAndIntercept[FailedToCreateException](testService.createNewOrgUser(testOrgAccount))
      }
    }
  }
}
