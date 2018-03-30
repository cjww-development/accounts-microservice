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
package controllers

import javax.inject.Inject

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.security.encryption.DataSecurity
import common.BackendController
import models.FeedItem
import play.api.libs.json.JsObject
import play.api.mvc.{Action, AnyContent}
import services.UserFeedService

import scala.concurrent.ExecutionContext.Implicits.global

class UserFeedControllerImpl @Inject()(val userFeedService: UserFeedService,
                                       val authConnector: AuthConnector) extends UserFeedController

trait UserFeedController extends BackendController {
  val userFeedService: UserFeedService

  def createEvent() : Action[String] = Action.async(parse.text) { implicit request =>
    applicationVerification {
      withJsonBody[FeedItem](FeedItem.newFeedItemReads) { fi =>
        userFeedService.createFeedItem(fi) map(if(_) Ok else InternalServerError)
      }
    }
  }

  def retrieveFeed(userId: String) : Action[AnyContent] = Action.async { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        userFeedService.getFeedList(user.id) map {
          case Some(json) => Ok(DataSecurity.encryptType[JsObject](json))
          case None       => NotFound
        }
      }
    }
  }
}
