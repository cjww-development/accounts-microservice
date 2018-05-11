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

import com.cjwwdev.implicits.ImplicitDataSecurity._
import common._
import helpers.controllers.ControllerSpec
import play.api.test.FakeRequest

class UpdateUserDetailsControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new UpdateUserDetailsController {
      override val accountService = mockAccountService
      override val authConnector  = mockAuthConnector
    }
  }

  "updateProfileInformation" should {
    "return an Ok" when {
      "the users profile information has been updated" in new Setup {
        val request: FakeRequest[String] = standardRequest.withBody[String](testUserProfile.encryptType)

        mockUpdateProfileInformation(updated = true)

        runActionWithAuth(testController.updateProfileInformation(testUserId), request, "individual") {
          status(_) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem updating the users profile information" in new Setup {
        val request = standardRequest.withBody(testUserProfile.encryptType)

        mockUpdateProfileInformation(updated = false)

        runActionWithAuth(testController.updateProfileInformation(testUserId), request, "individual") {
          status(_) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }

  "updateUserPassword" should {
    "return an Ok" when {
      "the users password has been successfully updated" in new Setup {
        val request = standardRequest.withBody(testUpdatedPassword.encryptType)

        mockUpdatePassword(updatedResponse = PasswordUpdated)

        runActionWithAuth(testController.updateUserPassword(testUserId), request, "individual") {
          status(_) mustBe OK
        }
      }
    }

    "return a conflict" when {
      "the users old password doesn't match what is on record" in new Setup {
        val request = standardRequest.withBody(testUpdatedPassword.encryptType)

        mockUpdatePassword(updatedResponse = InvalidOldPassword)

        runActionWithAuth(testController.updateUserPassword(testUserId), request, "individual") {
          status(_) mustBe CONFLICT
        }
      }
    }

    "return an Internal server error" when {
      "there was a problem updating the users password" in new Setup {
        val request = standardRequest.withBody(testUpdatedPassword.encryptType)

        mockUpdatePassword(updatedResponse = PasswordUpdateFailed)

        runActionWithAuth(testController.updateUserPassword(testUserId), request, "individual") {
          status(_) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }

  "updateUserSettings" should {
    "return an Ok" when {
      "a users settings have been updated" in new Setup {
        val request = standardRequest.withBody(testSettings.encryptType)

        mockUpdateSettings(updated = true)

        runActionWithAuth(testController.updateUserSettings(testUserId), request, "individual") {
          status(_) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there a problem updating the users settings" in new Setup {
        val request = standardRequest.withBody(testSettings.encryptType)

        mockUpdateSettings(updated = false)

        runActionWithAuth(testController.updateUserSettings(testUserId), request, "individual") {
          status(_) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
