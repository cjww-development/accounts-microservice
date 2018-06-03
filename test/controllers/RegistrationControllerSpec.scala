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

import com.cjwwdev.security.encryption.{DataSecurity, SHA512}
import helpers.controllers.ControllerSpec
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.stubControllerComponents

class RegistrationControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new RegistrationController {
      override protected def controllerComponents = stubControllerComponents()
      override val registrationService            = mockRegistrationService
      override val validationService              = mockValidationService
      override val authConnector                  = mockAuthConnector
    }
  }

  val password = SHA512.encrypt("testPassword")

  val testNewUserJson = Json.parse(
    s"""
       |{
       | "firstName" : "testFirstName",
       | "lastName" : "testLastName",
       | "userName" : "tUserName",
       | "email" : "test@email.com",
       | "password" : "${SHA512.encrypt("testPass")}"
       |}
    """.stripMargin
  )

  val testNewOrgUserJson = Json.parse(
    s"""
      |{
      | "orgName" : "testOrgName",
      | "initials" : "TON",
      | "orgUserName" : "tUserName",
      | "location" : "testLocation",
      | "orgEmail" : "test@email.com",
      | "password" : "$password"
      |}
    """.stripMargin
  )

  val encryptedUserJson = DataSecurity.encryptType[JsValue](testNewUserJson)
  val encryptedOrgUserJson = DataSecurity.encryptType[JsValue](testNewOrgUserJson)

  "createNewUser" should {
    "return a created" when {
      "a new account has been created" in new Setup {
        val request = standardRequest.withBody(encryptedUserJson)

        mockIsEmailInUse(inUse = false)

        mockIsUserNameInUse(inUse = false)

        mockCreateNewUser(created = true)

        runActionWithoutAuth(testController.createNewUser, request) {
          status(_) mustBe CREATED
        }
      }
    }

    "return an internal server error" when {
      "there was a problem creating a new account" in new Setup {
        val request = standardRequest.withBody(encryptedUserJson)

        mockIsEmailInUse(inUse = false)

        mockIsUserNameInUse(inUse = false)

        mockCreateNewUser(created = false)

        runActionWithoutAuth(testController.createNewUser, request) {
          status(_) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a conflict" when {
      "the user name and email are already in use" in new Setup {
        val request = standardRequest.withBody(encryptedUserJson)

        mockIsEmailInUse(inUse = true)

        mockIsUserNameInUse(inUse = true)

        mockCreateNewUser(created = false)

        runActionWithoutAuth(testController.createNewUser, request) {
          status(_) mustBe CONFLICT
        }
      }
    }
  }

  "createNewOrgUser" should {
    "return a created" when {
      "a new account has been created" in new Setup {
        val request = standardRequest.withBody(encryptedOrgUserJson)

        mockIsEmailInUse(inUse = false)

        mockIsUserNameInUse(inUse = false)

        mockCreateNewOrgUser(created = true)

        runActionWithoutAuth(testController.createNewOrgUser, request) {
          status(_) mustBe CREATED
        }
      }
    }

    "return an internal server error" when {
      "there was a problem creating a new account" in new Setup {
        val request = standardRequest.withBody(encryptedOrgUserJson)

        mockIsEmailInUse(inUse = false)

        mockIsUserNameInUse(inUse = false)

        mockCreateNewOrgUser(created = false)

        runActionWithoutAuth(testController.createNewOrgUser, request) {
          status(_) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a conflict" when {
      "either the user name or email is already in use" in new Setup {
        val request = standardRequest.withBody(encryptedOrgUserJson)

        mockIsEmailInUse(inUse = true)

        mockIsUserNameInUse(inUse = true)

        mockCreateNewOrgUser(created = false)

        runActionWithoutAuth(testController.createNewOrgUser, request) {
          status(_) mustBe CONFLICT
        }
      }
    }
  }
}
