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
package repositories

import com.cjwwdev.mongo.{MongoConnector, MongoFailedRead, MongoSuccessCreate, MongoSuccessRead}
import helpers.CJWWSpec
import models.{EventDetail, FeedItem, SourceDetail}
import org.joda.time.DateTime
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class UserFeedRepositorySpec extends CJWWSpec {

  val testFeedItem =
    FeedItem(
      Some("testFeedId"),
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

  val testList = List(testFeedItem)

  class Setup {
    val testRepo = new UserFeedRepository(mockMongoConnector)
  }

  "createFeedItem" should {
    "return a MongoSuccessCreate" when {
      "given a new FeedItem" in new Setup {
        when(mockMongoConnector.create[FeedItem](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessCreate))

        val result = await(testRepo.createFeedItem(testFeedItem))
      }
    }
  }

  "getFeedItems" should {
    "return an list of feed items" when {
      "given a userId" in new Setup {
        when(mockMongoConnector.readBulk[FeedItem](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessRead(testList)))

        val result = await(testRepo.getFeedItems("testUserId"))
        result mustBe Some(testList)
      }
    }

    "return None" when {
      "given a userId" in new Setup {
        when(mockMongoConnector.readBulk[FeedItem](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoFailedRead))

        val result = await(testRepo.getFeedItems("testUserId"))
        result mustBe None
      }
    }
  }
}
