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
import models.TeacherDetails
import play.api.mvc.{Action, AnyContent, Controller}
import services.OrgAccountService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class OrgAccountController @Inject()(orgAccountService: OrgAccountService, authConnect: AuthConnector) extends Controller with Authorisation {

  val authConnector = authConnect

  def getOrganisationsTeachers(orgId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(orgId) {
        case Authorised =>
          orgAccountService.getOrganisationsTeachers(orgId) map { list =>
            Ok(DataSecurity.encryptType[List[TeacherDetails]](list).get)
          } recover {
            case _: Throwable => InternalServerError
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }
}
