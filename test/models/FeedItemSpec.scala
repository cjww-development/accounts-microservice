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

import java.util.UUID

import org.joda.time.DateTime
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsError, JsPath, JsString, Json}

class FeedItemSpec extends PlaySpec {
  val uuid = UUID.randomUUID
  val now  = DateTime.now

  "newFeedItemReads" should {
    "read a set of json into a new feed item" in {
      val newFeedItemJson = Json.parse(
        s"""
          |{
          | "userId" : "user-$uuid",
          | "sourceDetail" : {
          |   "service" : "auth-service",
          |   "location" : "test-location"
          | },
          | "eventDetail" : {
          |   "title" : "testTitle",
          |   "description" : "testDescription"
          | }
          |}
        """.stripMargin
      )

      val result = Json.fromJson(newFeedItemJson)(FeedItem.newFeedItemReads).get
      result.feedId.contains("feed-item") mustBe true
      result.userId mustBe s"user-$uuid"
      result.sourceDetail.service mustBe "auth-service"
      result.sourceDetail.location mustBe "test-location"
      result.eventDetail.title mustBe "testTitle"
      result.eventDetail.description mustBe "testDescription"
    }

    "return a JsError" when {
      "the user id is invalid" in {
        val newFeedItemJson = Json.parse(
          s"""
             |{
             | "userId" : "user-invalid",
             | "sourceDetail" : {
             |   "service" : "auth-service",
             |   "location" : "test-location"
             | },
             | "eventDetail" : {
             |   "title" : "testTitle",
             |   "description" : "testDescription"
             | }
             |}
        """.stripMargin
        )

        val result = Json.fromJson(newFeedItemJson)(FeedItem.newFeedItemReads)
        result.isError mustBe true
      }

      "the source detail service is invalid" in {
        val newFeedItemJson = Json.parse(
          s"""
             |{
             | "userId" : "user-$uuid",
             | "sourceDetail" : {
             |   "service" : "invalid-service",
             |   "location" : "test-location"
             | },
             | "eventDetail" : {
             |   "title" : "testTitle",
             |   "description" : "testDescription"
             | }
             |}
        """.stripMargin
        )

        val result = Json.fromJson(newFeedItemJson)(FeedItem.newFeedItemReads)
        result.isError mustBe true
      }

      "the source detail service and user id are invalid" in {
        val newFeedItemJson = Json.parse(
          s"""
             |{
             | "userId" : "user-invalid",
             | "sourceDetail" : {
             |   "service" : "invalid-service",
             |   "location" : "test-location"
             | },
             | "eventDetail" : {
             |   "title" : "testTitle",
             |   "description" : "testDescription"
             | }
             |}
        """.stripMargin
        )

        val result = Json.fromJson(newFeedItemJson)(FeedItem.newFeedItemReads)
        result.isError mustBe true
      }
    }
  }
}
