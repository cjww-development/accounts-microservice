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

import com.cjwwdev.reactivemongo.{MongoConnector, MongoCreateResponse, MongoRepository, MongoSuccessCreate}
import config.Exceptions.FailedToCreateException
import models.FeedItem
import play.api.libs.json.OFormat
import reactivemongo.api.DB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserFeedRepository @Inject()() extends MongoConnector {
  val store = new UserFeedRepo(db)
}

class UserFeedRepo(db: () => DB) extends MongoRepository("user-feed", db) {

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
      unique = true,
      sparse = false
    )
  )

  private def userIdSelector(userId: String): BSONDocument = BSONDocument("userId" -> userId)

  def createFeedItem(feedItem : FeedItem)(implicit format : OFormat[FeedItem]) : Future[MongoCreateResponse] = {
    collection.insert(feedItem) map { writeResult =>
      if(writeResult.ok) MongoSuccessCreate else throw new FailedToCreateException("Failed to create feed item")
    }
  }

  def getFeedItems(userId : String) : Future[List[FeedItem]] = {
    collection.find(userIdSelector(userId)).cursor[FeedItem]().collect[List]()
  }
}
