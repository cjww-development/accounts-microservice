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
package models

import com.cjwwdev.security.encryption.SHA512
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class UpdatedPasswordSpec extends PlaySpec {
  "standardFormat" should {
    "read a set of valid json into an UpdatedPassword" in {
      val updatedPasswordJson = Json.parse(
        s"""
          |{
          | "previousPassword" : "${SHA512.encrypt("testPass")}",
          | "newPassword" : "${SHA512.encrypt("testPass123")}"
          |}
        """.stripMargin
      )

      val updatedPassword = UpdatedPassword(
        previousPassword = SHA512.encrypt("testPass"),
        newPassword = SHA512.encrypt("testPass123")
      )

      Json.fromJson[UpdatedPassword](updatedPasswordJson) mustBe JsSuccess(updatedPassword)
    }

    "return a JsError" when {
      "the length isn't 128 chars" in {
        val updatedPasswordJson = Json.parse(
          s"""
             |{
             | "previousPassword" : "testPass",
             | "newPassword" : "testPass123"
             |}
        """.stripMargin
        )

        Json.fromJson[UpdatedPassword](updatedPasswordJson).isError mustBe true
      }
    }
  }
}
