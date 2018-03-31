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

class ValidationAPIISpec extends IntegrationSpec with IntegrationStubbing {
  s"/validate/user-name/:username" should {
    "return an Ok" when {
      "the user name being validated isn't in use" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/validate/user-name/${"newUserName".encrypt}").head()) {
          _.status mustBe OK
        }
      }
    }

    "return a Conflict" when {
      "the user name being validated is in use (individual)" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/validate/user-name/${testUserAccount.userName.encrypt}").head()) {
          _.status mustBe CONFLICT
        }
      }

      "the user name is in use (organisation)" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/validate/user-name/${testOrgAccount.orgUserName.encrypt}").head()) {
          _.status mustBe CONFLICT
        }
      }
    }
  }

  s"/validate/email/:email" should {
    "return an Ok" when {
      "the email address being validated is not in use" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/validate/email/${"testing@email.com".encrypt}").head()) {
          _.status mustBe OK
        }
      }
    }

    "return a Conflict" when {
      "the email being validated is in use (individual)" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/validate/email/${testUserAccount.email.encrypt}").head()) {
          _.status mustBe CONFLICT
        }
      }

      "the email being validated is in use (organisation)" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        awaitAndAssert(client(s"$testAppUrl/validate/email/${testOrgAccount.orgEmail.encrypt}").head()) {
          _.status mustBe CONFLICT
        }
      }
    }
  }
}
