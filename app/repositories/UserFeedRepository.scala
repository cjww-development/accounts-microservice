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

import javax.inject.{Inject, Singleton}

import com.cjwwdev.mongo._
import config.ApplicationConfiguration
import models.FeedItem
import play.api.libs.json.OFormat
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserFeedRepository @Inject()(mongoConnector: MongoConnector) extends ApplicationConfiguration {
  def createFeedItem(feedItem : FeedItem)(implicit format : OFormat[FeedItem]) : Future[MongoCreateResponse] = {
    mongoConnector.create[FeedItem](USER_FEED, feedItem.withId)
  }

  def getFeedItems(userId : String) : Future[Option[List[FeedItem]]] = {
    val query = BSONDocument("userId" -> userId)
    mongoConnector.readBulk[FeedItem](USER_FEED, query) map {
      case MongoSuccessRead(result) => Some(result.asInstanceOf[List[FeedItem]])
      case MongoFailedRead => None
    }
  }
}
