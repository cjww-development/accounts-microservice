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
import com.cjwwdev.mongo.responses.MongoSuccessCreate
import common.BackendController
import javax.inject.Inject
import models.{OrgAccount, UserAccount}
import play.api.mvc.{Action, ControllerComponents}
import services.{RegistrationService, ValidationService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DefaultRegistrationController @Inject()(val registrationService : RegistrationService,
                                              val validationService: ValidationService,
                                              val controllerComponents: ControllerComponents,
                                              val config: ConfigurationLoader,
                                              val authConnector: AuthConnector) extends RegistrationController {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait RegistrationController extends BackendController {
  val registrationService: RegistrationService
  val validationService: ValidationService

  def createNewUser : Action[String] = Action.async(parse.text) { implicit request =>
    applicationVerification {
      parsers.withJsonBody[UserAccount] { user =>
        for {
          userNameInUse <- validationService.isUserNameInUse(user.userName)
          emailInUse    <- validationService.isEmailInUse(user.email)
          registered    <- if(!userNameInUse & !emailInUse) {
            registrationService.createNewUser(user) map { resp =>
              val (status, body) = if(resp.equals(MongoSuccessCreate)) {
                (CREATED, "User created")
              } else {
                (INTERNAL_SERVER_ERROR, "There was a problem creating a new user")
              }

              withJsonResponseBody(status, body) { json =>
                status match {
                  case CREATED               => Created(json)
                  case INTERNAL_SERVER_ERROR => InternalServerError(json)
                }
              }
            }
          } else {
            withFutureJsonResponseBody(CONFLICT, "Could not create a new user; either the user name or email address is already in use") { json =>
              Future(Conflict(json))
            }
          }
        } yield registered
      }
    }
  }

  def createNewOrgUser: Action[String] = Action.async(parse.text) { implicit request =>
    applicationVerification {
      parsers.withJsonBody[OrgAccount] { orgUser =>
        for {
          userNameInUse <- validationService.isUserNameInUse(orgUser.orgUserName)
          emailInUse    <- validationService.isEmailInUse(orgUser.orgEmail)
          registered    <- if(!userNameInUse & !emailInUse) {
            registrationService.createNewOrgUser(orgUser) map { resp =>
              val (status, body) = if(resp.equals(MongoSuccessCreate)) {
                (CREATED, "User created")
              } else {
                (INTERNAL_SERVER_ERROR, "There was a problem creating a new user")
              }

              withJsonResponseBody(status, body) { json =>
                status match {
                  case CREATED               => Created(json)
                  case INTERNAL_SERVER_ERROR => InternalServerError(json)
                }
              }
            }
          } else {
            withFutureJsonResponseBody(CONFLICT, "Could not create a new user; either the user name or email address is already in use") { json =>
              Future(Conflict(json))
            }
          }
        } yield registered
      }
    }
  }
}
