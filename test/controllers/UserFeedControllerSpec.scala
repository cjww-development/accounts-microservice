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
package controllers

import com.cjwwdev.implicits.ImplicitDataSecurity._
import helpers.controllers.ControllerSpec
import play.api.test.Helpers.stubControllerComponents

class UserFeedControllerSpec extends ControllerSpec {

  class Setup {
    val testController = new UserFeedController {
      override protected def controllerComponents = stubControllerComponents()
      override val userFeedService                = mockUserFeedService
      override val authConnector                  = mockAuthConnector
    }
  }

  "createEvent" should {
    val request = standardRequest.withBody(testFeedItem.encryptType)

    "return an ok" when {
      "a feed event has been created" in new Setup {
        mockCreateFeedItem(created = true)

        runActionWithoutAuth(testController.createEvent(), request) {
          status(_) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem creating a feed item" in new Setup {
        mockCreateFeedItem(created = false)

        runActionWithoutAuth(testController.createEvent(), request) {
          status(_) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }
  }

  "retrieveFeed" should {
    "return an Ok" when {
      "a users feed has been found" in new Setup {
        mockGetFeedList(fetched = true)

        runActionWithAuth(testController.retrieveFeed(testUserId), standardRequest, "individual") {
          status(_) mustBe OK
        }
      }
    }

    "return a not found" when {
      "a users feed wasn't found" in new Setup {
        mockGetFeedList(fetched = false)

        runActionWithAuth(testController.retrieveFeed(testUserId), standardRequest, "individual") {
          status(_) mustBe NOT_FOUND
        }
      }
    }
  }
}
