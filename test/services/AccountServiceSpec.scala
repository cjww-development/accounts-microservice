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

import com.cjwwdev.mongo.responses.{MongoFailedUpdate, MongoSuccessUpdate}
import common._
import helpers.services.ServiceSpec
import models.{Settings, UpdatedPassword, UserProfile}

class AccountServiceSpec extends ServiceSpec {

  class Setup {
    val testService = new AccountService {
      override val userAccountRepository = mockUserAccountRepo
    }
  }

  "updateProfileInformation" should {
    "return a MongoSuccessUpdate" in new Setup {
      mockUpdateAccountData(updated = true)

      awaitAndAssert(testService.updateProfileInformation(generateTestSystemId(USER), testUserProfile)) {
        _ mustBe MongoSuccessUpdate
      }
    }

    "return a MongoFailedUpdate" in new Setup {
      mockUpdateAccountData(updated = false)

      awaitAndIntercept[FailedToUpdateException](testService.updateProfileInformation(generateTestSystemId(USER), testUserProfile))
    }
  }

  "updatePassword" should {
    "return an InvalidOldPassword" in new Setup {
      mockFindPassword(found = false)

      awaitAndAssert(testService.updatePassword(generateTestSystemId(USER), testUpdatedPassword)) {
        _ mustBe InvalidOldPassword
      }
    }

    "return a PasswordUpdated" in new Setup {
      mockFindPassword(found = true)

      mockUpdatePassword(updated = true)

      awaitAndAssert(testService.updatePassword(generateTestSystemId(USER), testUpdatedPassword)) {
        _ mustBe PasswordUpdated
      }
    }

    "return a PasswordUpdateFailed" in new Setup {
      mockFindPassword(found = true)

      mockUpdatePassword(updated = false)

      awaitAndAssert(testService.updatePassword(generateTestSystemId(USER), testUpdatedPassword)) {
        _ mustBe PasswordUpdateFailed
      }
    }
  }

  "updateSettings" should {
    "return an UpdatedSettingsFailed" in new Setup {
      mockUpdateSettings(updated = false)

      awaitAndAssert(testService.updateSettings(generateTestSystemId(USER), testSettings)) {
        _ mustBe UpdatedSettingsFailed
      }
    }

    "return an UpdatedSettingsSuccess" in new Setup {
      mockUpdateSettings(updated = true)

      awaitAndAssert(testService.updateSettings(generateTestSystemId(USER), testSettings)) {
        _ mustBe UpdatedSettingsSuccess
      }
    }
  }
}
