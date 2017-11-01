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
package controllers.test

import javax.inject.{Inject, Singleton}

import com.cjwwdev.auth.actions.Authorisation
import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.config.ConfigurationLoader
import play.api.mvc.{Action, AnyContent, Controller}
import services.TestEndpointService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TeardownController @Inject()(testEndpointService: TestEndpointService,
                                   val authConnector: AuthConnector,
                                   val config: ConfigurationLoader) extends Controller with Authorisation {

  private val INDIVIDUAL    = "individual"
  private val ORGANISATION  = "organisation"

  def tearDownUser(userName: String, credentialType: String): Action[AnyContent] = Action.async { implicit request =>
    credentialType match {
      case INDIVIDUAL   => testEndpointService.tearDownTestUser(userName) map(_ => Ok)
      case ORGANISATION => testEndpointService.tearDownTestOrgUser(userName) map(_ => Ok)
    }
  }
}
