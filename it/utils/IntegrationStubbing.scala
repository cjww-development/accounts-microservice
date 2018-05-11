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

import com.cjwwdev.auth.models.CurrentUser
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.cjwwdev.security.encryption.DataSecurity
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, stubFor, urlEqualTo}
import models.{FeedItem, OrgAccount, UserAccount}
import play.api.libs.json.Json

trait IntegrationStubbing {
  self: IntegrationSpec =>

  class PreconditionBuilder {
    implicit val builder: PreconditionBuilder = this

    def user: UserStub = UserStub()
  }

  def given: PreconditionBuilder = new PreconditionBuilder

  case class UserStub()(implicit builder: PreconditionBuilder) {
    def individualUser: IndividualUser = IndividualUser()
    def orgUser: OrgUser = OrgUser()
  }

  case class IndividualUser()(implicit builder: PreconditionBuilder) {
    def isSetup: PreconditionBuilder = {
      await(userAccountRepository.collection flatMap(_.insert[UserAccount](testUserAccount)))
      builder
    }

    def isAuthorised: PreconditionBuilder = {
      stubFor(get(urlEqualTo(s"/session-store/session/$testCookieId/data?key=contextId"))
        .willReturn(
          aResponse()
            .withStatus(200)
            .withBody(Json.prettyPrint(testApiResponse(
              s"/session-store/session/$testCookieId/data?key=contextId",
              "GET",
              OK,
              testContextId.encrypt
            )))
        )
      )
      stubbedGet(
        s"/auth/get-current-user/${generateTestSystemId(CONTEXT)}",
        OK,
        Json.prettyPrint(testApiResponse(
          s"/auth/get-current-user/${generateTestSystemId(CONTEXT)}",
          "GET",
          OK,
          DataSecurity.encryptType[CurrentUser](testCurrentUser)
        ))
      )
      builder
    }

    def hasFeedItems: PreconditionBuilder = {
      await(userFeedRepository.collection.flatMap(_.insert[FeedItem](testFeedItem)))
      await(userFeedRepository.collection.flatMap(_.insert[FeedItem](testFeedItem2)))
      builder
    }
  }

  case class OrgUser()(implicit builder: PreconditionBuilder) {
    def isSetup: PreconditionBuilder = {
      await(orgAccountRepository.collection flatMap(_.insert[OrgAccount](testOrgAccount)))
      builder
    }

    def hasTeachers: PreconditionBuilder = {
      await(userAccountRepository.insertNewUser(testUserAccount(AccountEnums.teacher)))
      builder
    }

    def isAuthorised: PreconditionBuilder = {
      stubFor(get(urlEqualTo(s"/session-store/session/$testCookieId/data?key=contextId"))
        .willReturn(
          aResponse()
            .withStatus(OK)
            .withBody(Json.prettyPrint(testApiResponse(
              s"/session-store/session/$testCookieId/data?key=contextId",
              "GET",
              OK,
              testContextId.encrypt
            )))
        )
      )
      stubbedGet(
        s"/auth/get-current-user/${generateTestSystemId(CONTEXT)}",
        OK,
        Json.prettyPrint(testApiResponse(
          s"/auth/get-current-user/${generateTestSystemId(CONTEXT)}",
          "GET",
          OK,
          DataSecurity.encryptType[CurrentUser](testOrgCurrentUser)
        ))
      )
      builder
    }
  }
}
