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

import com.cjwwdev.security.encryption.DataSecurity
import helpers.CJWWSpec
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ValidationService

import scala.concurrent.Future

class ValidationControllerSpec extends CJWWSpec {

  val testEncUserName = DataSecurity.encryptString("testUserName")
  val testEncEmail    = DataSecurity.encryptString("test@email.com")

  class Setup {
    val testController = new ValidationController(mockValidationService, mockConfig, mockAuthConnector)
  }

  "validateUserName" should {
    "return an OK" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_SERVICE_ID)

      when(mockValidationService.isUserNameInUse(ArgumentMatchers.eq("testUserName")))
        .thenReturn(Future.successful(false))

      val result = testController.validateUserName(testEncUserName)(request)
      status(result) mustBe OK
    }

    "return a CONFLICT" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_SERVICE_ID)

      when(mockValidationService.isUserNameInUse(ArgumentMatchers.eq("testUserName")))
        .thenReturn(Future.successful(true))

      val result = testController.validateUserName(testEncUserName)(request)
      status(result) mustBe CONFLICT
    }

    "return an BAD REQUEST" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_SERVICE_ID)

      val result = testController.validateUserName("INVALID_STRING")(request)
      status(result) mustBe BAD_REQUEST
    }

    "return a FORBIDDEN" in new Setup {
      val request = FakeRequest()

      val result = testController.validateUserName(testEncUserName)(request)
      status(result) mustBe FORBIDDEN
    }
  }

  "validateEmail" should {
    "return an OK" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_SERVICE_ID)

      when(mockValidationService.isEmailInUse(ArgumentMatchers.eq("test@email.com")))
        .thenReturn(Future.successful(false))

      val result = testController.validateEmail(testEncEmail)(request)
      status(result) mustBe OK
    }

    "return a CONFLICT" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_SERVICE_ID)

      when(mockValidationService.isEmailInUse(ArgumentMatchers.eq("test@email.com")))
        .thenReturn(Future.successful(true))

      val result = testController.validateEmail(testEncEmail)(request)
      status(result) mustBe CONFLICT
    }

    "return an BAD REQUEST" in new Setup {
      val request = FakeRequest().withHeaders("appID" -> AUTH_SERVICE_ID)

      val result = testController.validateEmail("INVALID_STRING")(request)
      status(result) mustBe BAD_REQUEST
    }

    "return a FORBIDDEN" in new Setup {
      val request = FakeRequest()

      val result = testController.validateEmail(testEncUserName)(request)
      status(result) mustBe FORBIDDEN
    }
  }
}