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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.security.deobfuscation.DeObfuscation._
import com.cjwwdev.security.obfuscation.Obfuscation._
import com.cjwwdev.implicits.ImplicitDataSecurity._
import common.AdminUtils
import javax.inject.Inject
import play.api.mvc.{Action, ControllerComponents}
import services.AdminService

import scala.concurrent.ExecutionContext

class DefaultAdminController @Inject()(val controllerComponents: ControllerComponents,
                                       val adminService: AdminService,
                                       val config: ConfigurationLoader) extends AdminController {
  override val adminId: String               = config.getServiceId("admin-frontend")
  override val appId: String                 = config.getServiceId(config.get[String]("appName"))
  override implicit val ec: ExecutionContext = controllerComponents.executionContext
}

trait AdminController extends AdminUtils {

  val adminService: AdminService

  def getIdByUsername(encUserName: String): Action[String] = Action.async(parse.text) { implicit req =>
    applicationVerification {
      withEncryptedUrl[String](encUserName) { userName =>
        adminService.getIdByUsername(userName) map { someId =>
          val (status, body) = someId.fold(NOT_FOUND -> "No user for this user name")(id => OK -> id.encrypt)

          withJsonResponseBody(status, body) { json =>
            Status(status)(json)
          }
        }
      }
    }
  }
}
