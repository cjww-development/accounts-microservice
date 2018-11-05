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

package utils

import akka.util.Timeout
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.testing.integration.IntegrationTestSpec
import com.cjwwdev.testing.integration.application.IntegrationApplication
import com.cjwwdev.testing.integration.wiremock.WireMockSetup
import models.UserAccount
import org.joda.time.LocalDateTime
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSRequest
import repositories._

import scala.concurrent.duration._

trait IntegrationSpec
  extends IntegrationTestSpec
    with Fixtures
    with IntegrationApplication
    with WireMockSetup {

  override implicit def defaultAwaitTimeout: Timeout = 5.seconds

  val testContextId   = generateTestSystemId(CONTEXT)
  val testOrgId       = generateTestSystemId(ORG)
  val testUserId      = generateTestSystemId(USER)
  val testDeversityId = generateTestSystemId(DEVERSITY)

  val testUserAccount: UserAccount = testUserAccount(AccountEnums.teacher)

  override val appConfig = Map(
    "play.http.router"                                        -> "testRouter.Routes",
    "microservice.external-services.auth-microservice.domain" -> s"$wiremockUrl/auth",
    "microservice.external-services.auth-microservice.uri"    -> "/get-current-user/:sessionId",
    "microservice.external-services.session-store.domain"     -> s"$wiremockUrl/session-store",
    "microservice.external-services.session-store.uri"        -> "/session/:contextId/data?key=contextId",
    "repositories.UserAccountRepositoryImpl.collection"       -> "it-user-accounts",
    "repositories.OrgAccountRepositoryImpl.collection"        -> "it-org-accounts",
    "repositories.UserFeedRepositoryImpl.collection"          -> "it-user-feed"
  )

  override val currentAppBaseUrl = "accounts"

  lazy val userAccountRepository = app.injector.instanceOf[UserAccountRepository]
  lazy val orgAccountRepository  = app.injector.instanceOf[OrgAccountRepository]
  lazy val userFeedRepository    = app.injector.instanceOf[UserFeedRepository]

  val testCookieId = generateTestSystemId(SESSION)

  def client(url: String): WSRequest = ws.url(url).withHeaders(
    "cjww-headers" -> HeaderPackage("abda73f4-9d52-4bb8-b20d-b5fffd0cc130", Some(testCookieId)).encrypt,
    CONTENT_TYPE  -> TEXT
  )

  def testApiResponse(uri: String, method: String, status: Int, body: String): JsValue = Json.obj(
    "uri"    -> s"$uri",
    "method" -> s"$method",
    "status" -> status,
    "body"   -> s"$body",
    "stats"  -> Json.obj(
      "requestCompletedAt" -> s"${LocalDateTime.now}"
    )
  )

  private def afterITest(): Unit = {
    userAccountRepository.collection.flatMap(_.drop(failIfNotFound = false))
    orgAccountRepository.collection.flatMap(_.drop(failIfNotFound = false))
    userFeedRepository.collection.flatMap(_.drop(failIfNotFound = false))
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWm()
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWm()
  }

  override def afterEach(): Unit = {
    super.afterEach()
    afterITest()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    afterITest()
    stopWm()
  }
}
