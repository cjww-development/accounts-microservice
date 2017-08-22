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
package controllers

import javax.inject.{Inject, Singleton}

import com.cjwwdev.auth.actions.Authorisation
import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.identifiers.IdentifierValidation
import com.cjwwdev.request.RequestParsers
import com.cjwwdev.security.encryption.DataSecurity
import models.FeedItem
import play.api.libs.json.JsObject
import play.api.mvc.{Action, AnyContent, Controller}
import services.UserFeedService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserFeedController @Inject()(userFeedService: UserFeedService,
                                   val config: ConfigurationLoader,
                                   val authConnector: AuthConnector) extends Controller with RequestParsers with Authorisation with IdentifierValidation {

  def createEvent() : Action[String] = Action.async(parse.text) {
    implicit request =>
      openActionVerification {
        withJsonBody[FeedItem](FeedItem.newFeedItemReads) { fi =>
          userFeedService.createFeedItem(fi) map(created => if(created) Ok else InternalServerError)
        }
      }
  }

  def retrieveFeed(userId: String) : Action[AnyContent] = Action.async { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { context =>
        userFeedService.getFeedList(context.user.userId) map {
          case Some(json) => Ok(DataSecurity.encryptType[JsObject](json))
          case None       => NotFound
        }
      }
    }
  }
}
