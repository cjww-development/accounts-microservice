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

import javax.inject.Inject

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.reactivemongo._
import common.FailedToCreateException
import models.FeedItem
import play.api.libs.json.OFormat
import reactivemongo.api.Cursor
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserFeedRepositoryImpl @Inject()(val configurationLoader: ConfigurationLoader) extends UserFeedRepository

trait UserFeedRepository extends MongoDatabase {
  private val MAX = 10

  override def indexes: Seq[Index] = Seq(
    Index(
      key = Seq("feedId" -> IndexType.Ascending),
      name = Some("FeedId"),
      unique = true,
      sparse = false
    ),
    Index(
      key = Seq("userId" -> IndexType.Ascending),
      name = Some("UserId"),
      unique = false,
      sparse = false
    )
  )

  private def userIdSelector(userId: String): BSONDocument = BSONDocument("userId" -> userId)

  def createFeedItem(feedItem : FeedItem)(implicit format : OFormat[FeedItem]) : Future[MongoCreateResponse] = {
    collection flatMap {
      _.insert(feedItem) map { wr =>
        if(wr.ok) MongoSuccessCreate else throw new FailedToCreateException("Failed to create feed item")
      }
    }
  }

  def getFeedItems(userId : String) : Future[List[FeedItem]] = {
    collection flatMap {
      _.find(userIdSelector(userId)).cursor[FeedItem]().collect[List](MAX, Cursor.FailOnError[List[FeedItem]]())
    }
  }

  def deleteFeedItems(userId: String): Future[MongoDeleteResponse] = {
    collection.flatMap {
      _.remove(userIdSelector(userId)) map { wr =>
        if(wr.ok) MongoSuccessDelete else MongoFailedDelete
      }
    }
  }
}
