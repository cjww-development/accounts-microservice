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

import javax.inject.Inject

import com.cjwwdev.auth.connectors.AuthConnector
import common.BackendController
import play.api.mvc.{Action, AnyContent}
import services.ValidationService

import scala.concurrent.ExecutionContext.Implicits.global

class ValidationControllerImpl @Inject()(val validationService : ValidationService,
                                         val authConnector: AuthConnector) extends ValidationController

trait ValidationController extends BackendController {
  val validationService: ValidationService

  def validateUserName(username : String) : Action[AnyContent] = Action.async { implicit request =>
    openActionVerification {
      withEncryptedUrl(username) { userName =>
        validationService.isUserNameInUse(userName) map { inUse =>
          if(!inUse) Ok else Conflict
        }
      }
    }
  }

  def validateEmail(email : String) : Action[AnyContent] = Action.async { implicit request =>
    openActionVerification {
      withEncryptedUrl(email) { emailAddress =>
        validationService.isEmailInUse(emailAddress) map { inUse =>
          if(!inUse) Ok else Conflict
        }
      }
    }
  }
}
