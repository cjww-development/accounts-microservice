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

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.Obfuscation._
import common.BackendController
import javax.inject.Inject
import models.FeedItem
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.UserFeedService

import scala.concurrent.ExecutionContext

class DefaultUserFeedController @Inject()(val userFeedService: UserFeedService,
                                          val controllerComponents: ControllerComponents,
                                          val config: ConfigurationLoader,
                                          val authConnector: AuthConnector,
                                          implicit val ec: ExecutionContext) extends UserFeedController {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait UserFeedController extends BackendController {
  val userFeedService: UserFeedService

  def createEvent() : Action[String] = Action.async(parse.text) { implicit request =>
    applicationVerification {
      parsers.withJsonBody[FeedItem] { fi =>
        userFeedService.createFeedItem(fi) map { created =>
          val (status, body) = if(created) (OK, "Event created") else (INTERNAL_SERVER_ERROR, "There was a problem creating the event")
          withJsonResponseBody(status, body) { json =>
            status match {
              case OK                    => Ok(json)
              case INTERNAL_SERVER_ERROR => InternalServerError(json)
            }
          }
        }
      }
    }
  }

  def retrieveFeed(userId: String) : Action[AnyContent] = Action.async { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        userFeedService.getFeedList(user.id) map { list =>
          val (status, body) = list.fold((NOT_FOUND, "No feed items are available"))(json => (OK, json.encrypt))
          withJsonResponseBody(status, body) { json =>
            status match {
              case OK        => Ok(json)
              case NOT_FOUND => NotFound(json)
            }
          }
        }
      }
    }
  }
}
