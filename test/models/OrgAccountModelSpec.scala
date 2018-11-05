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
package models

import com.cjwwdev.implicits.ImplicitDataSecurity._
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, Json}

class OrgAccountModelSpec extends PlaySpec {
  "newOrgAccountReads" should {
    "read a set of json in a new org account" in {
      val newUserJson = Json.parse(
        s"""
          |{
          | "orgName" : "testOrgName",
          | "initials" : "TON",
          | "orgUserName" : "tUserName",
          | "location" : "testLocation",
          | "orgEmail" : "test@email.com",
          | "password" : "${"testPass".sha512}"
          |}
        """.stripMargin
      )

      val result = Json.fromJson(newUserJson)(OrgAccount.newOrgAccountReads).get
      result.orgId.contains("org-user-") mustBe true
      result.orgName                     mustBe "testOrgName"
      result.initials                    mustBe "TON"
      result.orgUserName                 mustBe "tUserName"
      result.location                    mustBe "testLocation"
      result.orgEmail                    mustBe "test@email.com"
      result.credentialType              mustBe "organisation"
      result.password.length             mustBe 128
      result.settings                    mustBe None
    }

    "return a JsError" when {
      "given a set of invalid json" in {
        val newUserJson = Json.parse(
          """
             |{
             | "orgName" : "testOrgNameytfkjkjfjyrdrfghjuytfrdfghjuytrdfvhjuytrdfgvbhjuytrdfcvbhjuyt6rfg",
             | "initials" : "ERDFTGHJKJUHYT",
             | "orgUserName" : "testUserNameInvalid",
             | "location" : "testLocationgytfghjiuytrdfcgvhuiytresdfghuytrdxfcgvhuytrdexg",
             | "orgEmail" : "test-invalid",
             | "password" : "testPass"
             |}
          """.stripMargin
        )

        val result = Json.fromJson(newUserJson)(OrgAccount.newOrgAccountReads)
        result.getClass mustBe classOf[JsError]
      }
    }
  }
}
