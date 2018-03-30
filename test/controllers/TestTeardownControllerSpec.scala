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

class TestTeardownControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new TestTeardownController {
      override val testEndpointService = mockTestEndpointService
      override val authConnector       = mockAuthConnector
    }
  }

  "tearDownUser" should {
    "delete an individual user" when {
      "given a username and individial credential type" in new Setup {
        mockTearDownTestUser(tornDown = true)

        runActionWithoutAuth(testController.tearDownUser("testUsername", "individual"), standardRequest) {
          status(_) mustBe OK
        }
      }
    }

    "delete an organisation user" when {
      "given a username and organisation credential type" in new Setup {
        mockTearDownTestOrgUser(tornDown = true)

        runActionWithoutAuth(testController.tearDownUser("testUserName", "organisation"), standardRequest) {
          status(_) mustBe OK
        }
      }
    }
  }
}
