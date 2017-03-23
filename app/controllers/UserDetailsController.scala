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

import com.cjwwdev.security.encryption.DataSecurity
import models.{BasicDetails, Enrolments, Settings}
import play.api.Environment
import play.api.mvc.{Action, AnyContent}
import services.GetDetailsService
import utils.application.{Authorised, BackendController, NotAuthorised}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserDetailsController @Inject()(detailsService: GetDetailsService) extends BackendController {
  def getBasicDetails(userId: String) : Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          detailsService.getBasicDetails(userId) map {
            case Some(details) => Ok(DataSecurity.encryptData[BasicDetails](details).get)
            case None => NotFound
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def getEnrolments(userId: String) : Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          detailsService.getEnrolments(userId) map {
            case Some(enrolments) => Ok(DataSecurity.encryptData[Enrolments](enrolments).get)
            case None => NotFound
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def getSettings(userId : String) : Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          detailsService.getSettings(userId) map {
            case Some(settings) => Ok(DataSecurity.encryptData[Settings](settings).get)
            case None => NotFound
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }
}
