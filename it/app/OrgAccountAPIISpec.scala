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

import com.cjwwdev.security.encryption.DataSecurity
import models.TeacherDetails
import utils.{IntegrationSpec, IntegrationStubbing}

class OrgAccountAPIISpec extends IntegrationSpec with IntegrationStubbing {
  s"/account/${testOrgAccount.orgId}/teachers" should {
    "return an Ok" when {
      "a list of teacher details have been found for the org id" in {
        given
            .user.orgUser.isSetup
            .user.orgUser.hasTeachers
            .user.orgUser.isAuthorised

        awaitAndAssert(client(s"$testAppUrl/account/${testOrgAccount.orgId}/teachers").get()) { res =>
          res.status mustBe OK
          DataSecurity.decryptIntoType[List[TeacherDetails]](res.body).get.size mustBe 1
        }
      }
    }
  }
}
