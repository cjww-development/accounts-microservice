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

package controllers

import helpers.controllers.ControllerSpec
import play.api.test.Helpers._

class OrgAccountControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new OrgAccountController {
      override protected def controllerComponents = stubControllerComponents()
      override val orgAccountService              = mockOrgAccountService
      override val authConnector                  = mockAuthConnector
      override val appId                          = "testAppId"
    }
  }

  "getOrganisationsTeachers" should {
    "return an ok" when {
      "given a valid orgId" in new Setup {
        mockGetOrganisationsTeachers(populated = true)

        runActionWithAuth(testController.getOrganisationsTeachers(testOrgId), standardRequest, "organisation") {
          status(_) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem getting the list of teachers" in new Setup {
        mockGetOrganisationsTeachers(populated = false)

        runActionWithAuth(testController.getOrganisationsTeachers(testOrgId), standardRequest, "organisation") {
          status(_) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
