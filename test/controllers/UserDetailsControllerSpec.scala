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
import play.api.test.Helpers.stubControllerComponents

class UserDetailsControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new UserDetailsController {
      override protected def controllerComponents = stubControllerComponents()
      override val detailsService                 = mockGetDetailsService
      override val orgDetailsService              = mockOrgAccountService
      override val authConnector                  = mockAuthConnector
      override val appId                          = "testAppID"
    }
  }

  "getBasicDetails" should {
    "return an ok" when {
      "the users basic details have been found" in new Setup {
        mockGetBasicDetails

        runActionWithAuth(testController.getBasicDetails(testUserId), standardRequest, "individual") {
          status(_) mustBe OK
        }
      }
    }
  }

  "getEnrolments" should {
    "return an ok" when {
      "the users enrolments have been found" in new Setup {
        mockGetEnrolments(fetched = true)

        runActionWithAuth(testController.getEnrolments(testUserId), standardRequest, "individual") {
          status(_) mustBe OK
        }
      }
    }

    "return a not found" when {
      "no enrolments have been found" in new Setup {
        mockGetEnrolments(fetched = false)

        runActionWithAuth(testController.getEnrolments(testUserId), standardRequest, "individual") {
          status(_) mustBe NOT_FOUND
        }
      }
    }
  }

  "getSettings" should {
    "return an ok" when {
      "the users settings have been found" in new Setup {
        mockGetSettings(fetched = true)

        runActionWithAuth(testController.getSettings(testUserId), standardRequest, "individual") {
          status(_) mustBe OK
        }
      }
    }

    "return a not found" when {
      "no settings have been found" in new Setup {
        mockGetSettings(fetched = false)

        runActionWithAuth(testController.getSettings(testUserId), standardRequest, "individual") {
          status(_) mustBe NOT_FOUND
        }
      }
    }
  }

  "getOrgBasicDetails" should {
    "return an OK" when {
      "an orgs basic details are found" in new Setup {
        mockGetOrganisationBasicDetails(fetched = true)

        runActionWithAuth(testController.getOrgBasicDetails(testOrgId), standardRequest, "organisation") {
          status(_) mustBe OK
        }
      }
    }

    "return a not found" when {
      "no basic details are found for an org" in new Setup {
        mockGetOrganisationBasicDetails(fetched = false)

        runActionWithAuth(testController.getOrgBasicDetails(testOrgId), standardRequest, "organisation") {
          status(_) mustBe NOT_FOUND
        }
      }
    }
  }
}
