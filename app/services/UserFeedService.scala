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

import javax.inject.{Inject, Singleton}

import com.cjwwdev.mongo.{MongoFailedCreate, MongoSuccessCreate}
import models.FeedItem
import play.api.libs.json.{JsObject, Json}
import repositories.UserFeedRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserFeedService @Inject()(userFeedRepository : UserFeedRepository) {
  private val MIN = 0
  private val MAX = 10

  def createFeedItem(feedItem: FeedItem) : Future[Boolean] = {
    userFeedRepository.createFeedItem(feedItem) map {
      case MongoSuccessCreate => false
      case MongoFailedCreate => true
    }
  }

  def flipList(list : Option[List[FeedItem]]) : Option[List[FeedItem]] = {
    list match {
      case Some(feedList) => Some(feedList.reverse.slice(MIN, MAX))
      case None => None
    }
  }

  def getFeedList(userId : String) : Future[Option[JsObject]] = {
    userFeedRepository.getFeedItems(userId) map(list => convertToJsObject(flipList(list)))
  }

  private def convertToJsObject(list : Option[List[FeedItem]]) : Option[JsObject] = {
    for {
      fi <- list
    } yield Json.obj("feed-array" -> fi)
  }
}
