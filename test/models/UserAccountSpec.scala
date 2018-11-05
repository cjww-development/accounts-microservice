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

class UserAccountSpec extends PlaySpec {
  "newAccountReads" should {
    "read a set of valid json into a new UserAccount" in {
      val newUserJson = Json.parse(
        s"""
          |{
          | "firstName" : "testFirstName",
          | "lastName" : "testLastName",
          | "userName" : "tUserName",
          | "email" : "test@email.com",
          | "password" : "${"testString".sha512}"
          |}
        """.stripMargin
      )

      val result = Json.fromJson(newUserJson)(UserAccount.newUserReads).get
      result.userId.contains("user-") mustBe true
      result.firstName mustBe "testFirstName"
      result.lastName mustBe "testLastName"
      result.userName mustBe "tUserName"
      result.email mustBe "test@email.com"
      result.password.length mustBe 128
      result.deversityDetails mustBe None
      result.enrolments mustBe None
      result.settings mustBe None
    }

    "return a JsError" when {
      "given a set of invalid json to read" in {
        val newUserJson = Json.parse(
          s"""
             |{
             | "firstName" : "testFirstNamewefwefwefweklfweflkerkgjwelirbe",
             | "lastName" : "testLastNameieurhgwlekuhrglkqehrglkwjehrglkwjerhglkjwherlgkjwherlkjbekqergrgjbekjgwberg",
             | "userName" : "testUserNameInvalid",
             | "email" : "invalid-email",
             | "password" : "testString"
             |}
        """.stripMargin
        )

        val result = Json.fromJson(newUserJson)(UserAccount.newUserReads)
        result.getClass mustBe classOf[JsError]
      }
    }
  }
}
