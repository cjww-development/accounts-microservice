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
import common.BackendController
import play.api.mvc.{Action, AnyContent}
import services.TestEndpointService

import scala.concurrent.ExecutionContext.Implicits.global

class TestTeardownControllerImpl @Inject()(val testEndpointService: TestEndpointService,
                                           val authConnector: AuthConnector) extends TestTeardownController

trait TestTeardownController extends BackendController {
  val testEndpointService: TestEndpointService

  private val INDIVIDUAL    = "individual"
  private val ORGANISATION  = "organisation"

  def tearDownUser(userName: String, credentialType: String): Action[AnyContent] = Action.async { implicit request =>
    credentialType match {
      case INDIVIDUAL   => testEndpointService.tearDownTestUser(userName) map(_ => Ok)
      case ORGANISATION => testEndpointService.tearDownTestOrgUser(userName) map(_ => Ok)
    }
  }
}
