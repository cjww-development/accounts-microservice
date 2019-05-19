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
import com.cjwwdev.security.obfuscation.Obfuscation._
import models._
import org.joda.time.DateTime
import play.api.libs.json.{JsObject, JsValue, Json}

trait Fixtures extends TestDataGenerator {

  object AccountEnums extends Enumeration {
    val basic     = Value
    val teacher   = Value
    val student   = Value
  }

  val testOrgDevId = generateTestSystemId(DEVERSITY)

  val testOrgCurrentUser = CurrentUser(
    contextId       = generateTestSystemId(CONTEXT),
    id              = generateTestSystemId(ORG),
    orgDeversityId  = Some(generateTestSystemId(DEVERSITY)),
    credentialType  = "organisation",
    orgName         = None,
    firstName       = None,
    lastName        = None,
    role            = None,
    enrolments      = None
  )

  val testCurrentUser = CurrentUser(
    contextId       = generateTestSystemId(CONTEXT),
    id              = generateTestSystemId(USER),
    orgDeversityId  = Some(generateTestSystemId(DEVERSITY)),
    credentialType  = "individual",
    orgName         = None,
    firstName       = Some("testFirstName"),
    lastName        = Some("testLastName"),
    role            = None,
    enrolments      = Some(Json.obj(
      "deversityId" -> generateTestSystemId(DEVERSITY)
    ))
  )

  def testTeacherEnrolment: DeversityEnrolment = {
    DeversityEnrolment(
      schoolDevId     = testOrgDevId,
      role            = "teacher",
      title           = Some("testTitle"),
      room            = Some("testRoom"),
      teacher         = None
    )
  }

  def testStudentEnrolment: DeversityEnrolment = {
    DeversityEnrolment(
      schoolDevId     = testOrgDevId,
      role            = "student",
      title           = None,
      room            = None,
      teacher         = Some(createTestUserName)
    )
  }

  def testUserAccount(accountType: AccountEnums.Value): UserAccount = {
    val accType = if(accountType == AccountEnums.teacher) {
      Some(testTeacherEnrolment)
    } else if(accountType == AccountEnums.student) {
      Some(testStudentEnrolment)
    } else {
      None
    }

    val enrs = if(accountType == AccountEnums.teacher | accountType == AccountEnums.student) {
      Some(Enrolments(None, None, Some(generateTestSystemId(DEVERSITY))))
    } else {
      None
    }

    UserAccount(
      userId            = generateTestSystemId(USER),
      firstName         = "testFirstName",
      lastName          = "testLastName",
      userName          = "testUserName",
      email             = "test@email.com",
      password          = "testPass",
      deversityDetails  = accType,
      createdAt         = now,
      enrolments        = enrs,
      settings          = None
    )
  }

  val testBasicDetails = BasicDetails(
    firstName = "testFirstName",
    lastName  = "testLastName",
    userName  = "testUserName",
    email     = "test@email.com",
    createdAt = now
  )

  val testEnrolments = Enrolments(
    deversityId = Some(generateTestSystemId(DEVERSITY)),
    hubId       = None,
    diagId      = None
  )

  val testSettings = Settings(
    displayName       = "full",
    displayNameColour = "#000000",
    displayImageURL   = ""
  )

  val testOrgAccount = OrgAccount(
    orgId           = generateTestSystemId(ORG),
    deversityId     = testOrgDevId,
    orgName         = "testSchoolName",
    initials        = "TSN",
    orgUserName     = "tSchoolName",
    location        = "testLocation",
    orgEmail        = "test-org@email.com",
    credentialType  = "organisation",
    password        = "testPass".sha512,
    createdAt       = now,
    settings        = None
  )

  val testOrgDetails = OrgDetails(
    orgName  = testOrgAccount.orgName,
    initials = testOrgAccount.initials,
    location = testOrgAccount.location
  )

  val testTeacherDetails = TeacherDetails(
    userId   = generateTestSystemId(USER),
    title    = "testTitle",
    lastName = "testLastName",
    room     = "testRoom"
  )

  val testFeedItem = FeedItem(
    feedId = generateTestSystemId(FEED_ITEM),
    userId = generateTestSystemId(USER),
    sourceDetail = SourceDetail(
      service   = "auth-service",
      location  = "testLocation"
    ),
    eventDetail = EventDetail(
      title       = "testTitle",
      description = "testDesc"
    ),
    generated = now
  )

  val testFeedItem2 = FeedItem(
    "testFeedItemId",
    generateTestSystemId(USER),
    SourceDetail(
      "auth-service",
      "testLocation"
    ),
    EventDetail(
      "testTitle",
      "testDescription"
    ),
    DateTime.now()
  )

  val testJsonObj = Json.parse(
    """{
      |   "feedId" : "testFeedId",
      |   "userId" : "testUserId",
      |   "sourceDetail" : {
      |     "service" : "testService",
      |     "location" : "testLocation"
      |   },
      |   "eventDetail" : {
      |     "title" : "testTitle",
      |     "description" : "testDescription"
      |   },
      |   "generated" : "2017-10-10T12:00:00Z"
      |}""".stripMargin
    ).as[JsObject]

  val testFeedList = List(testFeedItem2, testFeedItem)

  val testUpdatedPassword = UpdatedPassword(
    previousPassword = "testOldPassword".sha512,
    newPassword      = "testNewPassword".sha512
  )

  val testUserProfile = UserProfile(
    firstName = "testFirstName",
    lastName  = "testLastName",
    userName  = "tUserName",
    email     = "test@email.com",
    settings  = None
  )

  val testNewUserJson = Json.parse(
    s"""
       |{
       | "firstName" : "testFirstName",
       | "lastName" : "testLastName",
       | "userName" : "tUserName",
       | "email" : "test@email.com",
       | "password" : "${"testPass".sha512}"
       |}
    """.stripMargin
  )

  val encryptedUserJson = testNewUserJson.encrypt

  val testNewOrgUserJson = Json.parse(
    s"""
       |{
       | "orgName" : "testSchoolName",
       | "initials" : "TSN",
       | "orgUserName" : "tSchoolName",
       | "location" : "testLocation",
       | "orgEmail" : "test@email.com",
       | "password" : "${"testPass".sha512}"
       |}
    """.stripMargin
  )

  val encryptedOrgUserJson = testNewOrgUserJson.encrypt

  val testEncFeedItem = Json.parse(
    s"""{
       |   "userId" : "${generateTestSystemId(USER)}",
       |   "sourceDetail" : {
       |     "service" : "auth-service",
       |     "location" : "testLocation"
       |   },
       |   "eventDetail" : {
       |     "title" : "testTitle",
       |     "description" : "testDescription"
       |   },
       |   "generated" : "2017-10-10T12:00:00Z"
       |}""".stripMargin
  ).encrypt

  val testFeedArray = Json.obj("feed-array" -> Json.toJson(testFeedList))
}
