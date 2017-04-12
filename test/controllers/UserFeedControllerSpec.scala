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
import mocks.AuthBuilder
import models.{EventDetail, FeedItem, SourceDetail}
import org.joda.time.DateTime
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import play.api.test.FakeRequest

import scala.concurrent.Future

class UserFeedControllerSpec extends CJWWSpec {

  val testFeedItem =
    FeedItem(
      Some("testFeedId"),
      "testUserId",
      SourceDetail("testService","testLocation"),
      EventDetail("testTitle","testDescription"),
      DateTime.now()
    )

  val testFeedItemJson = Json.toJson(testFeedItem).as[JsObject]

  class Setup {
    val testController = new UserFeedController(mockUserFeedService, mockAuthConnector)
  }

  "createEvent" should {
    "return an ok" when {
      "a feed item has been successfully created" in new Setup {
        when(mockUserFeedService.createFeedItem(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody(DataSecurity.encryptData[FeedItem](testFeedItem).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.createEvent(), mockAuthConnector, request) {
          result => status(result) mustBe OK
        }
      }
    }

    "return an internal server error" when {
      "there was a problem creating the feed item" in new Setup {
        when(mockUserFeedService.createFeedItem(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            "appId" -> "abda73f4-9d52-4bb8-b20d-b5fffd0cc130",
            CONTENT_TYPE -> TEXT
          ).withBody(DataSecurity.encryptData[FeedItem](testFeedItem).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.createEvent(), mockAuthConnector, request) {
          result => status(result) mustBe INTERNAL_SERVER_ERROR
        }
      }
    }

    "return a forbidden" when {
      "there was no valid appId in the header" in new Setup {
        val request =
          FakeRequest().withSession(
            "cookieId"  -> "session-0987654321",
            "contextId" -> "context-1234567890",
            "firstName" -> "testFirstName",
            "lastName"  -> "testLastName"
          ).withHeaders(
            CONTENT_TYPE -> TEXT
          ).withBody(DataSecurity.encryptData[FeedItem](testFeedItem).get)

        AuthBuilder.buildAuthorisedUserAndPost[String](testController.createEvent(), mockAuthConnector, request) {
          result => status(result) mustBe FORBIDDEN
        }
      }
    }
  }

  "retrieveFeed" should {
    "return an ok" when {
      "list of feed items is found" in new Setup {
        when(mockUserFeedService.getFeedList(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testFeedItemJson)))

        AuthBuilder.buildAuthorisedUserAndGet(testController.retrieveFeed("user-766543"), mockAuthConnector) {
          result => status(result) mustBe OK
        }
      }
    }

    "return a not found" when {
      "no feed items are found" in new Setup {
        when(mockUserFeedService.getFeedList(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        AuthBuilder.buildAuthorisedUserAndGet(testController.retrieveFeed("user-766543"), mockAuthConnector) {
          result => status(result) mustBe NOT_FOUND
        }
      }
    }
  }
}
