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
package services

import com.cjwwdev.mongo.responses.{MongoFailedCreate, MongoSuccessCreate}
import javax.inject.Inject
import models.FeedItem
import play.api.libs.json.{JsObject, Json}
import repositories.UserFeedRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultUserFeedService @Inject()(val userFeedRepository : UserFeedRepository) extends UserFeedService

trait UserFeedService {
  val userFeedRepository: UserFeedRepository

  def createFeedItem(feedItem: FeedItem) : Future[Boolean] = {
    userFeedRepository.createFeedItem(feedItem) map {
      case MongoSuccessCreate => true
      case MongoFailedCreate  => false
    }
  }

  def flipList(list : List[FeedItem]) : Option[List[FeedItem]] = {
    if(list.nonEmpty) Some(list.reverse) else None
  }

  def getFeedList(userId : String) : Future[Option[JsObject]] = {
    userFeedRepository.getFeedItems(userId) map(list => convertToJsObject(flipList(list)))
  }

  private def convertToJsObject(list : Option[List[FeedItem]]) : Option[JsObject] = {
    list map(fi => Json.obj("feed-array" -> fi))
  }
}
