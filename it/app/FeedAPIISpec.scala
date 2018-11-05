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

package app

import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.implicits.ImplicitJsValues._
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import play.api.libs.json.JsObject
import utils.{IntegrationSpec, IntegrationStubbing}

class FeedAPIISpec extends IntegrationSpec with IntegrationStubbing {
  "/create-feed-item" should {
    "return an Ok" when {
      "a feed item has been created" in {
        awaitAndAssert(client(s"$testAppUrl/create-feed-item").post(testEncFeedItem)) {
          _.status mustBe OK
        }
      }
    }
  }

  s"/account/$testUserId/get-user-feed" should {
    "return an Ok" when {
      "a list of feed items has been found" in {
        given
          .user.individualUser.hasFeedItems
          .user.individualUser.isAuthorised

        awaitAndAssert(client(s"$testAppUrl/account/$testUserId/get-user-feed").get()) { res =>
          res.status                                     mustBe OK
          res.json.get[String]("body").decrypt[JsObject] mustBe Left(testFeedArray)
        }
      }
    }

    "return a NotFound" when {
      "no feed items could be found for the user" in {
        given
          .user.individualUser.isAuthorised

        awaitAndAssert(client(s"$testAppUrl/account/$testUserId/get-user-feed").get()) { res =>
          res.status mustBe NOT_FOUND
        }
      }
    }
  }
}
