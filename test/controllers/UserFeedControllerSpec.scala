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

import java.util.UUID

import com.cjwwdev.security.encryption.DataSecurity
import helpers.CJWWSpec
import mocks.AuthBuilder
import models.{EventDetail, FeedItem, SourceDetail}
import org.joda.time.DateTime
import play.api.test.Helpers._
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.libs.json.Json
import play.api.test.FakeRequest

import scala.concurrent.Future

class UserFeedControllerSpec extends CJWWSpec {

  class Setup {
    val testController = new UserFeedController(mockUserFeedService, mockAuthConnector)
  }

  val uuid = UUID.randomUUID

  val testFeedItem = FeedItem(
    s"feed-item-$uuid",
    s"user-$uuid",
    SourceDetail(
      "auth-service",
      "testLocation"
    ),
    EventDetail(
      "testTitle",
      "testDescription"
    ),
    DateTime.now()
  )


  "createEvent" should {
    val request = FakeRequest().withHeaders(
      "appId" -> AUTH_SERVICE_ID
    ).withBody(
      DataSecurity.encryptType[FeedItem](testFeedItem)
    )

    "return an ok" when {
      "a feed event has been created" in new Setup {

        when(mockUserFeedService.createFeedItem(ArgumentMatchers.any()))
          .thenReturn(Future.successful(true))

        val result = testController.createEvent()(request)
        status(result) mustBe OK
      }
    }

    "return an internal server error" when {
      "there was a problem creating a feed item" in new Setup {
        when(mockUserFeedService.createFeedItem(ArgumentMatchers.any()))
          .thenReturn(Future.successful(false))

        val result = testController.createEvent()(request)
        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "retrieveFeed" should {
    val request = buildRequest

    "return an Ok" when {
      "a users feed has been found" in new Setup {
        when(mockUserFeedService.getFeedList(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(Json.obj())))

        AuthBuilder.getWithAuthorisedUser(testController.retrieveFeed(s"user-$uuid"), request,  mockAuthConnector, uuid, "user") {
          result => status(result) mustBe OK
        }
      }
    }

    "return a not found" when {
      "a users feed wasn't found" in new Setup {
        when(mockUserFeedService.getFeedList(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        AuthBuilder.getWithAuthorisedUser(testController.retrieveFeed(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe NOT_FOUND
        }
      }
    }
  }
}
