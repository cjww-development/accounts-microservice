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
import com.cjwwdev.reactivemongo.{MongoFailedUpdate, MongoSuccessUpdate}
import com.cjwwdev.request.RequestParsers
import config._
import models.{Settings, UpdatedPassword, UserProfile}
import play.api.mvc.{Action, Controller}
import services._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UpdateUserDetailsController @Inject()(accountService: AccountService,
                                            val config: ConfigurationLoader,
                                            val authConnector: AuthConnector)
  extends Controller with RequestParsers with Authorisation with IdentifierValidation {

  def updateProfileInformation(userId: String) : Action[String] = Action.async(parse.text) {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) { context =>
          withJsonBody[UserProfile](UserProfile.standardFormat) { profile =>
            accountService.updateProfileInformation(context.user.userId, profile) map {
              case MongoSuccessUpdate => Ok
              case MongoFailedUpdate  => InternalServerError
            }
          }
        }
      }
  }

  def updateUserPassword(userId: String) : Action[String] = Action.async(parse.text) {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) { context =>
          withJsonBody[UpdatedPassword](UpdatedPassword.standardFormat) { passwordSet =>
            accountService.updatePassword(context.user.userId, passwordSet) map {
              case PasswordUpdated      => Ok
              case InvalidOldPassword   => Conflict
              case PasswordUpdateFailed => InternalServerError
            }
          }
        }
      }
  }

  def updateUserSettings(userId: String) : Action[String] = Action.async(parse.text) {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) { context =>
          withJsonBody[Settings](Settings.standardFormat) { settings =>
            accountService.updateSettings(context.user.userId, settings) map {
              case UpdatedSettingsSuccess => Ok
              case UpdatedSettingsFailed  => InternalServerError
            }
          }
        }
      }
  }
}
