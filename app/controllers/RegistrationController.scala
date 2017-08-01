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

import com.cjwwdev.auth.actions.BaseAuth
import com.cjwwdev.reactivemongo.{MongoFailedCreate, MongoSuccessCreate}
import com.cjwwdev.request.RequestParsers
import com.google.inject.{Inject, Singleton}
import models.{OrgAccount, UserAccount}
import play.api.Logger
import play.api.mvc.{Action, Controller}
import services.{RegistrationService, ValidationService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class RegistrationController @Inject()(registrationService : RegistrationService,
                                       validationService: ValidationService) extends Controller with RequestParsers with BaseAuth {

  def createNewUser : Action[String] = Action.async(parse.text) {
    implicit request =>
      openActionVerification {
        decryptRequest[UserAccount](UserAccount.newUserReads) { user =>
          for {
            userNameInUse <- validationService.isUserNameInUse(user.userName)
            emailInUse    <- validationService.isEmailInUse(user.email)
            registered    <- if(!userNameInUse & !emailInUse) {
              registrationService.createNewUser(user) map {
                case MongoSuccessCreate   => Created
                case MongoFailedCreate    => InternalServerError
              }
            } else {
              Future.successful(Conflict)
            }
          } yield registered
        }
      }
  }

  def createNewOrgUser: Action[String] = Action.async(parse.text) {
    implicit request =>
      openActionVerification {
        decryptRequest[OrgAccount](OrgAccount.newOrgAccountReads) { orgUser =>
          for {
            userNameInUse <- validationService.isUserNameInUse(orgUser.orgUserName)
            emailInUse    <- validationService.isEmailInUse(orgUser.orgEmail)
            registered    <- if(!userNameInUse & !emailInUse) {
              registrationService.createNewOrgUser(orgUser) map {
                case MongoSuccessCreate   => Created
                case MongoFailedCreate    => InternalServerError
              }
            } else {
              Future.successful(Conflict)
            }
          } yield registered
        }
      }
  }
}
