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
import com.cjwwdev.identifiers.IdentifierValidation
import com.cjwwdev.security.encryption.DataSecurity
import models.{BasicDetails, Enrolments, OrgDetails, Settings}
import play.api.mvc.{Action, AnyContent, Controller}
import services.{GetDetailsService, OrgAccountService}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserDetailsController @Inject()(detailsService: GetDetailsService,
                                      orgDetailsService: OrgAccountService,
                                      authConnect: AuthConnector) extends Controller with Authorisation with IdentifierValidation {

  val authConnector: AuthConnector = authConnect

  def getBasicDetails(userId: String) : Action[AnyContent] = Action.async {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) {
          detailsService.getBasicDetails(userId) map { details =>
            Ok(DataSecurity.encryptType[BasicDetails](details))
          } recover {
            case _: Throwable => NotFound
          }
        }
      }
  }

  def getEnrolments(userId: String) : Action[AnyContent] = Action.async {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) {
          detailsService.getEnrolments(userId) map {
            case Some(enrolments) => Ok(DataSecurity.encryptType[Enrolments](enrolments))
            case None             => NotFound
          }
        }
      }
  }

  def getSettings(userId : String) : Action[AnyContent] = Action.async {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) {
          detailsService.getSettings(userId) map {
            case Some(settings) => Ok(DataSecurity.encryptType[Settings](settings))
            case None           => NotFound
          }
        }
      }
  }

  def getOrgBasicDetails(orgId: String): Action[AnyContent] = Action.async {
    implicit request =>
      validateAs(ORG_USER, orgId) {
        authorised(orgId) {
          orgDetailsService.getOrganisationBasicDetails(orgId) map {
            case Some(details) => Ok(DataSecurity.encryptType[OrgDetails](details))
            case None          => NotFound
          }
        }
      }
  }
}
