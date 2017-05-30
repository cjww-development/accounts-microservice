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
package app

import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{CREATED, FORBIDDEN}
import utils.CJWWIntegrationUtils

class RegistrationISpec extends CJWWIntegrationUtils {

  val testNewUserJson = Json.parse(
    """
      |{
      | "firstName" : "testFirstName",
      | "lastName" : "testLastName",
      | "userName" : "testUserName",
      | "email" : "test@email.com",
      | "password" : "testPass"
      |}
    """.stripMargin
  )

  val encryptedUserJson = DataSecurity.encryptType[JsValue](testNewUserJson).get

  val testNewOrgUserJson = Json.parse(
    """
      |{
      | "orgName" : "testOrgName",
      | "initials" : "TI",
      | "orgUserName" : "testOrgUserName",
      | "location" : "testLocation",
      | "orgEmail" : "test@email.com",
      | "password" : "testPass"
      |}
    """.stripMargin
  )

  val encryptedOrgUserJson = DataSecurity.encryptType[JsValue](testNewOrgUserJson).get

  "/account/create-new-user" should {
    "return a Created" when {
      "a new user has successfully been created" in {
        val request = client(s"$baseUrl/account/create-new-user")
          .withHeaders("appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130")
          .post(encryptedUserJson)

        val result = await(request)
        result.status mustBe CREATED

        afterITest()
      }
    }

    "return a Forbidden" when {
      "the request is not authorised" in {
        val request = client(s"$baseUrl/account/create-new-user")
          .post(encryptedUserJson)

        val result = await(request)
        result.status mustBe FORBIDDEN
      }
    }
  }

  "/account/create-new-org-user" should {
    "return a Created" when {
      "a new org user has been created" in {
        val request = client(s"$baseUrl/account/create-new-org-user")
          .withHeaders("appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130")
          .post(encryptedOrgUserJson)

        val result = await(request)
        result.status mustBe CREATED

        afterITest()
      }
    }

    "return a Forbidden" when {
      "the request is not authorised" in {
        val request = client(s"$baseUrl/account/create-new-org-user")
          .post(encryptedOrgUserJson)

        val result = await(request)
        result.status mustBe FORBIDDEN
      }
    }
  }
}
