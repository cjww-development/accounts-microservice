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

import utils.{IntegrationSpec, IntegrationStubbing}

class RegistrationAPIISpec extends IntegrationSpec with IntegrationStubbing {
  "/account/create-new-user" should {
    "return a Created" when {
      "a new user has successfully been created" in {
        awaitAndAssert(client(s"$testAppUrl/account/create-new-user").post(encryptedUserJson)) {
          _.status mustBe CREATED
        }
      }

      "return a Conflict" when {
        "an account already exists with either the same user name or email address" in {
          given
              .user.individualUser.isSetup

          awaitAndAssert(client(s"$testAppUrl/account/create-new-user").post(encryptedUserJson)) {
            _.status mustBe CONFLICT
          }
        }
      }

      "return a Forbidden" when {
        "the request is not authorised" in {
          awaitAndAssert(ws.url(s"$testAppUrl/account/create-new-user").post(encryptedUserJson)) {
            _.status mustBe FORBIDDEN
          }
        }
      }
    }
  }

  "/account/create-new-org-user" should {
    "return a Created" when {
      "a new org user has been created" in {
        awaitAndAssert(client(s"$testAppUrl/account/create-new-org-user").post(encryptedOrgUserJson)) {
          _.status mustBe CREATED
        }
      }
    }

    "return a Conflict" when {
      "an account already exists with either the same user name or email address" in {
        given
            .user.orgUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/account/create-new-org-user").post(encryptedOrgUserJson)) {
          _.status mustBe CONFLICT
        }
      }
    }

    "return a Forbidden" when {
      "the request is not authorised" in {
        awaitAndAssert(ws.url(s"$testAppUrl/account/create-new-org-user").post(encryptedOrgUserJson)) {
          _.status mustBe FORBIDDEN
        }
      }
    }
  }
}
