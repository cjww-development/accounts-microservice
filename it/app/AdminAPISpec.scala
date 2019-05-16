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

package app

import java.util.UUID

import com.cjwwdev.http.headers.HeaderPackage
import utils.{IntegrationSpec, IntegrationStubbing}
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.obfuscation.Obfuscation.stringObfuscate
import com.cjwwdev.security.deobfuscation.DeObfuscation.stringDeObfuscate
import play.api.libs.ws.WSRequest

class AdminAPISpec extends IntegrationSpec with IntegrationStubbing {

  override val currentAppBaseUrl = "private"

  "/user-name/:userName/user-id" should {
    "return an Ok" when {
      "a matching user has been found" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        val encUser = testUserAccount.userName.encrypt

        awaitAndAssert(client(s"$testAppUrl/user-name/$encUser/user-id").get()) { res =>
          res.status                                     mustBe OK
          res.json.\("body").as[String].decrypt.left.get mustBe testUserAccount.userId
        }
      }

      "a matching org user has been found" in {
        given
          .user.individualUser.isSetup
          .user.orgUser.isSetup

        val encUser = testOrgAccount.orgUserName.encrypt

        awaitAndAssert(client(s"$testAppUrl/user-name/$encUser/user-id").get()) { res =>
          res.status                                     mustBe OK
          res.json.\("body").as[String].decrypt.left.get mustBe testOrgAccount.orgId
        }
      }
    }

    "return a Not found" when {
      "no matching user could be found" in {
        val encUser = testOrgAccount.orgUserName.encrypt

        awaitAndAssert(client(s"$testAppUrl/user-name/$encUser/user-id").get()) { res =>
          res.status                            mustBe NOT_FOUND
          res.json.\("errorMessage").as[String] mustBe "No user for this user name"
        }
      }
    }

    "return a Forbidden" when {
      "the call doesn't contain the correct application id" in {
        val encUser = testOrgAccount.orgUserName.encrypt

        def client(url: String): WSRequest = ws.url(url).withHttpHeaders(
          "cjww-headers" -> HeaderPackage("invalid-app-id", Some(testCookieId)).encrypt,
          CONTENT_TYPE   -> TEXT,
          "requestId"    -> s"requestId-${UUID.randomUUID()}"
        )

        awaitAndAssert(client(s"$testAppUrl/user-name/$encUser/user-id").get()) { res =>
          res.status mustBe FORBIDDEN
        }
      }
    }
  }
}
