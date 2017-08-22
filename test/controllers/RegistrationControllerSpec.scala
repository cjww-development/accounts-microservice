// Copyright (C) 2016-2017 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package controllers

import java.util.UUID

import com.cjwwdev.reactivemongo.{MongoFailedCreate, MongoSuccessCreate}
import com.cjwwdev.security.encryption.{DataSecurity, SHA512}
import helpers.CJWWSpec
import mocks.AuthBuilder
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class RegistrationControllerSpec extends CJWWSpec {

  val uuid = UUID.randomUUID

  class Setup {
    val testController = new RegistrationController(mockRegService, mockValidationService, mockConfig)
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
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(encryptedUserJson)

        when(mockValidationService.isEmailInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockValidationService.isUserNameInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockRegService.createNewUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        AuthBuilder.postWithAuthorisedUser(testController.createNewUser, request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe CREATED
        }
      }
    }

    "return an internal server error" when {
      "there was a problem creating a new account" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(encryptedUserJson)

        when(mockValidationService.isEmailInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockValidationService.isUserNameInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockRegService.createNewUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        AuthBuilder.postWithAuthorisedUser(testController.createNewUser, request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a conflict" when {
      "the user name and email are already in use" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(encryptedUserJson)

        when(mockValidationService.isEmailInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockValidationService.isUserNameInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockRegService.createNewUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        AuthBuilder.postWithAuthorisedUser(testController.createNewUser, request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe CONFLICT
        }
      }
    }
  }

  "createNewOrgUser" should {
    "return a created" when {
      "a new account has been created" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(encryptedOrgUserJson)

        when(mockValidationService.isEmailInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockValidationService.isUserNameInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockRegService.createNewOrgUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        AuthBuilder.postWithAuthorisedUser(testController.createNewOrgUser, request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe CREATED
        }
      }
    }

    "return an internal server error" when {
      "there was a problem creating a new account" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(encryptedOrgUserJson)

        when(mockValidationService.isEmailInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockValidationService.isUserNameInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        when(mockRegService.createNewOrgUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        AuthBuilder.postWithAuthorisedUser(testController.createNewOrgUser, request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a conflict" when {
      "either the user name or email is already in use" in new Setup {
        val request = FakeRequest().withHeaders(
          "appId" -> AUTH_SERVICE_ID,
          CONTENT_TYPE -> TEXT
        ).withBody(encryptedOrgUserJson)

        when(mockValidationService.isEmailInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockValidationService.isUserNameInUse(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        when(mockRegService.createNewOrgUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        AuthBuilder.postWithAuthorisedUser(testController.createNewOrgUser, request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe CONFLICT
        }
      }
    }
  }
}
