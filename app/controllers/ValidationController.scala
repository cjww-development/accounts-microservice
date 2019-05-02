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
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import common.BackendController
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.ValidationService

import scala.concurrent.ExecutionContext

class DefaultValidationController @Inject()(val validationService : ValidationService,
                                            val controllerComponents: ControllerComponents,
                                            val config: ConfigurationLoader,
                                            val authConnector: AuthConnector,
                                            implicit val ec: ExecutionContext) extends ValidationController {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait ValidationController extends BackendController {
  val validationService: ValidationService

  def validateUserName(username : String) : Action[AnyContent] = Action.async { implicit request =>
    applicationVerification {
      withEncryptedUrl[String](username) { userName =>
        validationService.isUserNameInUse(userName) map { inUse =>
          val (status, body) = if(!inUse) (OK, "User name is available") else (CONFLICT, "User name is not available")
          withJsonResponseBody(status, body) { json =>
            status match {
              case OK       => Ok(json)
              case CONFLICT => Conflict(json)
            }
          }
        }
      }
    }
  }

  def validateEmail(email : String) : Action[AnyContent] = Action.async { implicit request =>
    applicationVerification {
      withEncryptedUrl[String](email) { emailAddress =>
        validationService.isEmailInUse(emailAddress) map { inUse =>
          val (status, body) = if(!inUse) (OK, "Email is not in use") else (CONFLICT, "Email is already in use")
          withJsonResponseBody(status, body) { json =>
            status match {
              case OK       => Ok(json)
              case CONFLICT => Conflict(json)
            }
          }
        }
      }
    }
  }
}
