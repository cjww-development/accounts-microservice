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
import common.BackendController
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.{GetDetailsService, OrgAccountService}

import scala.concurrent.ExecutionContext.Implicits.global

class DefaultUserDetailsController @Inject()(val detailsService: GetDetailsService,
                                             val orgDetailsService: OrgAccountService,
                                             val controllerComponents: ControllerComponents,
                                             val config: ConfigurationLoader,
                                             val authConnector: AuthConnector) extends UserDetailsController {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait UserDetailsController extends BackendController {
  val detailsService: GetDetailsService
  val orgDetailsService: OrgAccountService

  def getBasicDetails(userId: String) : Action[AnyContent] = Action.async { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        detailsService.getBasicDetails(user.id) map { details =>
          withJsonResponseBody(OK, details.encrypt) { json =>
            Ok(json)
          }
        } recover {
          case e: Throwable =>
            e.printStackTrace()
            withJsonResponseBody(NOT_FOUND, "No basic details found") { json =>
            NotFound(json)
          }
        }
      }
    }
  }

  def getEnrolments(userId: String) : Action[AnyContent] = Action.async { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        detailsService.getEnrolments(user.id) map { enrolments =>
          val (status, body) = enrolments.fold((NOT_FOUND, "No enrolments found"))(enr => (OK, enr.encrypt))
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

  def getSettings(userId : String) : Action[AnyContent] = Action.async { implicit request =>
    validateAs(USER, userId) {
      authorised(userId) { user =>
        detailsService.getSettings(user.id) map { settings =>
          val (status, body) = settings.fold((NOT_FOUND, "No settings found"))(s => (OK, s.encrypt))
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

  def getOrgBasicDetails(orgId: String): Action[AnyContent] = Action.async { implicit request =>
    validateAs(ORG_USER, orgId) {
      authorised(orgId) { user =>
        orgDetailsService.getOrganisationBasicDetails(user.id) map { details =>
          val (status, body) = details.fold((NOT_FOUND, "No basic details found"))(deets => (OK, deets.encrypt))
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
