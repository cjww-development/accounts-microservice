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

import javax.inject.Inject

import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.security.encryption.DataSecurity
import common.BackendController
import models.TeacherDetails
import play.api.mvc.{Action, AnyContent}
import services.OrgAccountService

import scala.concurrent.ExecutionContext.Implicits.global

class OrgAccountControllerImpl @Inject()(val orgAccountService: OrgAccountService,
                                         val authConnector: AuthConnector) extends OrgAccountController

trait OrgAccountController extends BackendController {
  val orgAccountService: OrgAccountService

  def getOrganisationsTeachers(orgId: String): Action[AnyContent] = Action.async { implicit request =>
    validateAs(ORG_USER, orgId) {
      authorised(orgId) { user =>
        orgAccountService.getOrganisationsTeachers(user.id) map { list =>
          Ok(DataSecurity.encryptType[List[TeacherDetails]](list))
        } recover {
          case _: Throwable => InternalServerError
        }
      }
    }
  }
}
