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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsSuccess, Json}

class SettingsSpec extends PlaySpec {
  "standardFormat" should {
    "read a set of json into a setting case class" in {
      val settingsJson = Json.parse(
        """
          |{
          | "displayName" : "full",
          | "displayNameColour" : "#FFFFFF",
          | "displayImageURL" : "/account-services/assets/images/background.jpg"
          |}
        """.stripMargin
      )

      val settings = Settings(
        displayName = "full",
        displayNameColour = "#FFFFFF",
        displayImageURL = "/account-services/assets/images/background.jpg"
      )

      Json.fromJson[Settings](settingsJson) mustBe JsSuccess(settings)
    }

    "read a set of json with a custom url into a setting case class" in {
      val settingsJson = Json.parse(
        """
          |{
          | "displayName" : "short",
          | "displayNameColour" : "#000000",
          | "displayImageURL" : "http://sample-images.com/test-image.jpg"
          |}
        """.stripMargin
      )

      val settings = Settings(
        displayName = "short",
        displayNameColour = "#000000",
        displayImageURL = "http://sample-images.com/test-image.jpg"
      )

      Json.fromJson[Settings](settingsJson) mustBe JsSuccess(settings)
    }

    "return a JsError" when {
      "an invalid displayName is presented" in {
        val settingsJson = Json.parse(
          """
            |{
            | "displayName" : "friend",
            | "displayNameColour" : "#FFFFFF",
            | "displayImageURL" : "http://sample-images.com/test-image.jpg"
            |}
          """.stripMargin
        )

        Json.fromJson[Settings](settingsJson).isError mustBe true
      }

      "an invalid displayNameColour is presented" in {
        val settingsJson = Json.parse(
          """
            |{
            | "displayName" : "full",
            | "displayNameColour" : "FFFFFF",
            | "displayImageURL" : "http://sample-images.com/test-image.jpg"
            |}
          """.stripMargin
        )

        Json.fromJson[Settings](settingsJson).isError mustBe true
      }

      "all fields are invalid" in {
        val settingsJson = Json.parse(
          """
            |{
            | "displayName" : "friend",
            | "displayNameColour" : "FFFFFF",
            | "displayImageURL" : "/test/uri"
            |}
          """.stripMargin
        )

        Json.fromJson[Settings](settingsJson).isError mustBe true
      }
    }
  }
}
