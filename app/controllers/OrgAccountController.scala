/*
 * Copyright 2019 CJWW Development
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
import common.BackendController
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.OrgAccountService

import scala.concurrent.ExecutionContext

class DefaultOrgAccountController @Inject()(val orgAccountService: OrgAccountService,
                                            val controllerComponents: ControllerComponents,
                                            val config: ConfigurationLoader,
                                            val authConnector: AuthConnector,
                                            implicit val ec: ExecutionContext) extends OrgAccountController {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait OrgAccountController extends BackendController {
  val orgAccountService: OrgAccountService

  def getOrganisationsTeachers(orgId: String): Action[AnyContent] = Action.async { implicit request =>
    validateAs(ORG_USER, orgId) {
      authorised(orgId) { user =>
        orgAccountService.getOrganisationsTeachers(user.id) map { list =>
          withJsonResponseBody(OK, list.encrypt) { json =>
            Ok(json)
          }
        } recover {
          case _: Throwable => withJsonResponseBody(INTERNAL_SERVER_ERROR, "There was a problem getting the organisations teachers") { json =>
            InternalServerError(json)
          }
        }
      }
    }
  }
}
