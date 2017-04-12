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

import com.cjwwdev.auth.actions.{Authorisation, Authorised, NotAuthorised}
import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.security.encryption.DataSecurity
import models.FeedItem
import play.api.libs.json.JsObject
import play.api.mvc.{Action, AnyContent}
import services.UserFeedService
import utils.application.BackendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserFeedController @Inject()(userFeedService: UserFeedService, authConnect: AuthConnector) extends BackendController with Authorisation {

  val authConnector = authConnect

  def createEvent() : Action[String] = Action.async(parse.text) {
    implicit request =>
      openActionVerification {
        case Authorised =>
          decryptRequest[FeedItem] { fi =>
            userFeedService.createFeedItem(fi) map {
              case true => InternalServerError
              case false => Ok
            }
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def retrieveFeed(userId: String) : Action[AnyContent] = Action.async {
    implicit request =>
      authorised(userId) {
        case Authorised =>
          userFeedService.getFeedList(userId) map {
            case Some(json) => Ok(DataSecurity.encryptData[JsObject](json).get)
            case None => NotFound
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }
}
