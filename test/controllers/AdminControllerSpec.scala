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

import helpers.controllers.ControllerSpec
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.Obfuscation.stringObfuscate
import com.cjwwdev.security.deobfuscation.DeObfuscation.stringDeObfuscate
import helpers.other.AccountEnums
import play.api.mvc.ControllerComponents
import services.AdminService
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext

class AdminControllerSpec extends ControllerSpec {

  val testController = new AdminController {
    override val adminService: AdminService = mockAdminService
    override val adminId: String = "abda73f4-9d52-4bb8-b20d-b5fffd0cc130"
    override implicit val ec: ExecutionContext = stubControllerComponents().executionContext
    override val appId: String = "testAppId"
    override protected def controllerComponents: ControllerComponents = stubControllerComponents()
  }

  val testEncUserName = "testUserName".encrypt

  "getIdByEmail" should {
    "return an Ok" when {
      "a matching user Id has been found" in {
        mockGetIdByUserName(found = true)

        val result = testController.getIdByUsername(testEncUserName)(standardRequest.withBody(""))
        status(result)                                              mustBe OK
        contentAsJson(result).\("body").as[String].decrypt.left.get mustBe "testId"
      }
    }

    "return a Not found" when {
      "no matching user could be found" in {
        mockGetIdByUserName(found = false)

        val result = testController.getIdByUsername(testEncUserName)(standardRequest.withBody(""))
        status(result)                                     mustBe NOT_FOUND
        contentAsJson(result).\("errorMessage").as[String] mustBe "No user for this user name"
      }
    }
  }
}
