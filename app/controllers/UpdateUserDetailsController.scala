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
import com.cjwwdev.mongo.responses.MongoSuccessUpdate
import common._
import javax.inject.Inject
import models.{Settings, UpdatedPassword, UserProfile}
import play.api.mvc.Action
import services._

import scala.concurrent.ExecutionContext.Implicits.global

class UpdateUserDetailsControllerImpl @Inject()(val accountService: AccountService,
                                                val authConnector: AuthConnector) extends UpdateUserDetailsController

trait UpdateUserDetailsController extends BackendController {
  val accountService: AccountService

  def updateProfileInformation(userId: String) : Action[String] = Action.async(parse.text) { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        withJsonBody[UserProfile](UserProfile.standardFormat) { profile =>
          accountService.updateProfileInformation(user.id, profile) map { resp =>
            val (status, body) = if(resp.equals(MongoSuccessUpdate)) {
              (OK, s"Profile for user has been updated")
            } else {
              (INTERNAL_SERVER_ERROR, s"There was a problem updating the profile for user")
            }

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
  }

  def updateUserPassword(userId: String) : Action[String] = Action.async(parse.text) { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        withJsonBody[UpdatedPassword](UpdatedPassword.standardFormat) { passwordSet =>
          accountService.updatePassword(user.id, passwordSet) map { resp =>
            val (status, body) = resp match {
              case PasswordUpdated      => (OK, s"Password for user has been updated")
              case InvalidOldPassword   => (CONFLICT, s"Password for user didn't match")
              case PasswordUpdateFailed => (INTERNAL_SERVER_ERROR, "There was a problem updating the users password")
            }

            withJsonResponseBody(status, body) { json =>
              status match {
                case OK                    => Ok(json)
                case CONFLICT              => Conflict(json)
                case INTERNAL_SERVER_ERROR => InternalServerError(json)
              }
            }
          }
        }
      }
    }
  }

  def updateUserSettings(userId: String) : Action[String] = Action.async(parse.text) { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        withJsonBody[Settings](Settings.standardFormat) { settings =>
          accountService.updateSettings(user.id, settings) map { resp =>
            val (status, body) = if(resp.equals(UpdatedSettingsSuccess)) {
              (OK, "Settings for user have been updated")
            } else {
              (INTERNAL_SERVER_ERROR, "There was a problem updating the settings for the user")
            }

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
  }
}
