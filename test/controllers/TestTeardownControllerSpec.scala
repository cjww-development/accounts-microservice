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

import com.cjwwdev.reactivemongo.MongoSuccessDelete
import helpers.CJWWSpec
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class TestTeardownControllerSpec extends CJWWSpec {

  class Setup {
    val testController = new TestTeardownController {
      override val testEndpointService = mockTestEndpointService
      override val authConnector       = mockAuthConnector
    }

    val request = FakeRequest().withHeaders(
      "appId"      -> AUTH_SERVICE_ID,
      CONTENT_TYPE -> TEXT
    )
  }

  "tearDownUser" should {
    "delete an individual user" when {
      "given a username and individial credential type" in new Setup {
        when(mockTestEndpointService.tearDownTestUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessDelete))

        val result = testController.tearDownUser("testUserName", "individual")(request)
        status(result) mustBe OK
      }
    }

    "delete an organisation user" when {
      "given a username and organisation credential type" in new Setup {
        when(mockTestEndpointService.tearDownTestOrgUser(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessDelete))

        val result = testController.tearDownUser("testUserName", "organisation")(request)
        status(result) mustBe OK
      }
    }
  }
}
