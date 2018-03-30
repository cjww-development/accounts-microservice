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

package app

import common.MissingAccountException
import models.OrgAccount
import reactivemongo.bson.BSONDocument
import utils.{IntegrationSpec, IntegrationStubbing}

class TestAPIISpec extends IntegrationSpec with IntegrationStubbing {
  "/test-only/test-user/:userName/credential-type/:credentialType/tear-down" should {
    "return an Ok" when {
      "a test individual user has been torn down" in {
        given
          .user.individualUser.isSetup

        awaitAndAssert(userAccountRepository.getUserBySelector(BSONDocument("userName" -> "testUserName"))) {
          _.userName mustBe "testUserName"
        }

        awaitAndAssert(client(s"$testAppUrl/test-only/test-user/testUserName/credential-type/individual/tear-down").delete()) {
          _.status mustBe OK
        }

        intercept[MissingAccountException](await(userAccountRepository.getUserBySelector(BSONDocument("userName" -> "testUserName"))))
      }

      "a test organisation user has been torn down" in {
        given
          .user.orgUser.isSetup

        awaitAndAssert(orgAccountRepository.getOrgAccount[OrgAccount](BSONDocument("orgUserName" -> "tSchoolName"))) {
          _.orgUserName mustBe "tSchoolName"
        }

        awaitAndAssert(client(s"$testAppUrl/test-only/test-user/tSchoolName/credential-type/organisation/tear-down").delete()) {
          _.status mustBe OK
        }

        intercept[MissingAccountException](await(orgAccountRepository.getOrgAccount[OrgAccount](BSONDocument("orgUserName" -> "tSchoolName"))))
      }
    }
  }
}
