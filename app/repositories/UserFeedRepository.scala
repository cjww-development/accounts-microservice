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

package repositories

import com.cjwwdev.mongo.DatabaseRepository
import com.cjwwdev.mongo.connection.ConnectionSettings
import com.cjwwdev.mongo.responses._
import common.FailedToCreateException
import javax.inject.Inject
import models.FeedItem
import play.api.Configuration
import play.api.libs.json.OFormat
import reactivemongo.api.Cursor
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json._

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultUserFeedRepository @Inject()(val config: Configuration) extends UserFeedRepository with ConnectionSettings

trait UserFeedRepository extends DatabaseRepository {
  private val MAX = 10

  override def indexes: Seq[Index] = Seq(
    Index(
      key    = Seq("feedId" -> IndexType.Ascending),
      name   = Some("FeedId"),
      unique = true,
      sparse = false
    ),
    Index(
      key    = Seq("userId" -> IndexType.Ascending),
      name   = Some("UserId"),
      unique = false,
      sparse = false
    )
  )

  def createFeedItem(feedItem: FeedItem)(implicit ec: ExC): Future[MongoCreateResponse] = {
    for {
      col <- collection
      wr  <- col.insert[FeedItem](feedItem)
    } yield if(wr.ok) MongoSuccessCreate else throw new FailedToCreateException("Failed to create feed item")
  }

  def getFeedItems(userId: String)(implicit ec: ExC): Future[List[FeedItem]] = {
    for {
      col   <- collection
      items <- col.find(userIdSelector(userId)).cursor[FeedItem]().collect[List](MAX, Cursor.FailOnError[List[FeedItem]]())
    } yield items
  }

  def deleteFeedItems(userId: String)(implicit ec: ExC): Future[MongoDeleteResponse] = {
    for {
      col <- collection
      wr  <- col.remove(userIdSelector(userId))
    } yield if(wr.ok) MongoSuccessDelete else MongoFailedDelete
  }
}
