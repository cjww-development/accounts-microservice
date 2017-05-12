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
package services

import com.cjwwdev.reactivemongo.{MongoFailedCreate, MongoSuccessCreate}
import helpers.CJWWSpec
import models.{EventDetail, FeedItem, SourceDetail}
import org.joda.time.DateTime
import repositories.{UserFeedRepo, UserFeedRepository}
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers
import play.api.libs.json.{JsObject, Json}

import scala.concurrent.Future

class UserFeedServiceSpec extends CJWWSpec {

  val testFeedItem =
    FeedItem(
      Some("testFeedItemId"),
      "testUserId",
      SourceDetail(
        "testService",
        "testLocation"
      ),
      EventDetail(
        "testTitle",
        "testDescription"
      ),
      DateTime.now()
    )

  val testFeedItem2 =
    FeedItem(
      Some("testFeedItemId"),
      "testUserId2",
      SourceDetail(
        "testService",
        "testLocation"
      ),
      EventDetail(
        "testTitle",
        "testDescription"
      ),
      DateTime.now()
    )

  val testJsonObj =
    Json.parse(
      """{
        |   "feedId" : "testfeedId",
        |   "userId" : "testUserId",
        |   "sourceDetail" : {
        |     "service" : "testService",
        |     "location" : "testLocation"
        |   },
        |   "eventDetail" : {
        |     "title" : "testTitle",
        |     "description" : "testDescription"
        |   },
        |   "generated" : "2017-10-10T12:00:00Z"
        |}""".stripMargin).as[JsObject]

  val testFeedList = List(testFeedItem, testFeedItem2)

  class Setup {
    val testService = new UserFeedService(mockUserFeedRepo) {
      override val userFeedStore: UserFeedRepo = mockUserFeedStore
    }
  }

  "createFeedItem" should {
    "return false" when {
      "the feed item has been successfully created" in new Setup {
        when(mockUserFeedStore.createFeedItem(ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = await(testService.createFeedItem(testFeedItem))
        result mustBe false
      }
    }

    "return true" when {
      "there was a problem creating the feed item" in new Setup {
        when(mockUserFeedStore.createFeedItem(ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedCreate))

        val result = await(testService.createFeedItem(testFeedItem))
        result mustBe true
      }
    }
  }

  "flipList" should {
    "return a reversed list" when {
      "given an optional list" in new Setup {
        val result = testService.flipList(testFeedList)
        result mustBe Some(testFeedList.reverse)
      }
    }

    "return none" when {
      "the given list is empty" in new Setup {
        val result = testService.flipList(List())
        result mustBe None
      }
    }
  }

  "getFeedList" should {
    "return a JsObject with a feed-array JsArray in it" when {
      "given a userId" in new Setup {
        when(mockUserFeedStore.getFeedItems(ArgumentMatchers.any()))
          .thenReturn(Future.successful(List(testFeedItem)))

        val result = await(testService.getFeedList("testUserId"))
        result.get.value.contains("feed-array") mustBe true
      }
    }
  }
}
