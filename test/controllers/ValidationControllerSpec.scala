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

class ValidationControllerSpec extends ControllerSpec {

  val testEncUserName = "testUserName".encrypt
  val testEncEmail    = "test@email.com".encrypt

  class Setup {
    val testController = new ValidationController {
      override val validationService = mockValidationService
      override val authConnector     = mockAuthConnector
    }
  }

  "validateUserName" should {
    "return an OK" in new Setup {
      mockIsUserNameInUse(inUse = false)

      runActionWithoutAuth(testController.validateUserName(testEncUserName), standardRequest) {
        status(_) mustBe OK
      }
    }

    "return a CONFLICT" in new Setup {
      mockIsUserNameInUse(inUse = true)

      runActionWithoutAuth(testController.validateUserName(testEncUserName), standardRequest) {
        status(_) mustBe CONFLICT
      }
    }

    "return an BAD REQUEST" in new Setup {
      runActionWithoutAuth(testController.validateUserName("INVALID_STRING"), standardRequest) {
        status(_) mustBe BAD_REQUEST
      }
    }
  }

  "validateEmail" should {
    "return an OK" in new Setup {
      mockIsEmailInUse(inUse = false)

      runActionWithoutAuth(testController.validateEmail(testEncEmail), standardRequest) {
        status(_) mustBe OK
      }
    }

    "return a CONFLICT" in new Setup {
      mockIsEmailInUse(inUse = true)

      runActionWithoutAuth(testController.validateEmail(testEncEmail), standardRequest) {
        status(_) mustBe CONFLICT
      }
    }

    "return an BAD REQUEST" in new Setup {
      runActionWithoutAuth(testController.validateEmail("INVALID_STRING"), standardRequest) {
       status(_) mustBe BAD_REQUEST
      }
    }
  }
}
