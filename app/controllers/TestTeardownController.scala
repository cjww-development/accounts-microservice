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
import common.BackendController
import javax.inject.Inject
import play.api.mvc._
import services.TestEndpointService

import scala.concurrent.ExecutionContext

class DefaultTestTeardownController @Inject()(val testEndpointService: TestEndpointService,
                                              val controllerComponents: ControllerComponents,
                                              val config: ConfigurationLoader,
                                              val authConnector: AuthConnector,
                                              implicit val ec: ExecutionContext) extends TestTeardownController {
  override val appId: String = config.getServiceId(config.get[String]("appName"))
}

trait TestTeardownController extends BackendController {
  val testEndpointService: TestEndpointService

  private val INDIVIDUAL    = "individual"
  private val ORGANISATION  = "organisation"

  private def jsonResponse(credentialType: String)(implicit request: Request[_]): Result = {
    val msg = credentialType match {
      case INDIVIDUAL   => "Test individual user has been torn down"
      case ORGANISATION => "Test organisation user has been torn down"
    }

    withJsonResponseBody(OK, msg) { json =>
      Ok(json)
    }
  }

  def tearDownUser(userName: String, credentialType: String): Action[AnyContent] = Action.async { implicit request =>
    credentialType match {
      case INDIVIDUAL   => testEndpointService.tearDownTestUser(userName) map(_ => jsonResponse(INDIVIDUAL))
      case ORGANISATION => testEndpointService.tearDownTestOrgUser(userName) map(_ => jsonResponse(ORGANISATION))
    }
  }
}
