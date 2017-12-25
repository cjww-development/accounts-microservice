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

import helpers.CJWWSpec
import mocks.AuthBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class OrgAccountControllerSpec extends CJWWSpec {

  class Setup {
    val testController = new OrgAccountController {
      override val orgAccountService = mockOrgAccountService
      override val authConnector     = mockAuthConnector
    }

    val request = FakeRequest().withHeaders(
      "appId"      -> AUTH_SERVICE_ID,
      CONTENT_TYPE -> TEXT
    )
  }

  val testOrgId = generateTestSystemId(ORG)

  "getOrganisationsTeachers" should {
    "return an ok" when {
      "given a valid orgId" in new Setup {
        when(mockOrgAccountService.getOrganisationsTeachers(ArgumentMatchers.any()))
          .thenReturn(Future.successful(List()))

        AuthBuilder.getWithAuthorisedUser(testController.getOrganisationsTeachers(testOrgId), request, mockAuthConnector, uuid, "org") {
          result =>
            status(result) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem getting the list of teachers" in new Setup {
        when(mockOrgAccountService.getOrganisationsTeachers(ArgumentMatchers.any()))
          .thenReturn(Future.failed(new Exception))

        AuthBuilder.getWithAuthorisedUser(testController.getOrganisationsTeachers(testOrgId), request, mockAuthConnector, uuid, "org") {
          result =>
            status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }
}
