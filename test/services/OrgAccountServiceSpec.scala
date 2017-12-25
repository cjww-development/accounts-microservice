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

package services

import java.util.UUID

import common.MissingAccountException
import helpers.CJWWSpec
import models._
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class OrgAccountServiceSpec extends CJWWSpec {

  val testOrgAccount = OrgAccount(
    orgId           = "org-test-org-id",
    deversityId     = "org-test-dev-id",
    orgName         = "testOrgName",
    initials        = "TI",
    orgUserName     = "testOrgUserName",
    location        = "testLocation",
    orgEmail        = "test@email.com",
    credentialType  = "organisation",
    password        = "testPass",
    createdAt       = now,
    settings        = None
  )

  val testOrgDetails = OrgDetails(
    orgName   = "testOrgName",
    initials  = "TI",
    location  = "testLocation"
  )

  val testAccount1 = UserAccount(
    userId            = "testUserId",
    firstName         = "testFirstName",
    lastName          = "testLastName",
    userName          = "testUserName",
    email             = "test@email.com",
    password          = "testPass",
    deversityDetails  = Some(DeversityEnrolment(
      statusConfirmed = "pending",
      schoolName      = "testOrgName",
      role            = "teacher",
      title           = Some("Prof"),
      room            = Some("testRoom"),
      teacher         = None
    )),
    createdAt         = now,
    enrolments        = None,
    settings          = None
  )

  val testAccount2 = UserAccount(
    userId            = "testUserId2",
    firstName         = "testFirstName2",
    lastName          = "testLastName2",
    userName          = "testUserName2",
    email             = "test2@email.com",
    password          = "testPass",
    deversityDetails  = Some(DeversityEnrolment(
      statusConfirmed = "pending",
      schoolName      = "testOrgName",
      role            = "teacher",
      title           = Some("Prof"),
      room            = Some("testRoom2"),
      teacher         = None
    )),
    createdAt         = now,
    enrolments        = None,
    settings          = None
  )

  val testAccountList = List(testAccount1, testAccount2)

  val testTeacherDetails1 = TeacherDetails(
    userId    = "testUserId",
    title     = "Prof",
    lastName  = "testLastName",
    room      = "testRoom",
    status    = "pending"
  )

  val testTeacherDetails2 = TeacherDetails(
    userId    = "testUserId2",
    title     = "Prof",
    lastName  = "testLastName2",
    room      = "testRoom2",
    status    = "pending"
  )

  val testTeacherDetailsList = List(testTeacherDetails1, testTeacherDetails2)

  class Setup {
    val testService = new OrgAccountService {
      override val orgAccountRepository  = mockOrgAccountRepo
      override val userAccountRepository = mockUserAccountRepo
    }
  }

  "getOrganisationsTeachers" should {
    "return a list of teacher details" in new Setup {
      when(mockOrgAccountRepo.getOrgAccount[OrgAccount](ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.successful(testOrgAccount))

      when(mockUserAccountRepo.getAllTeacherForOrg(ArgumentMatchers.any()))
        .thenReturn(Future.successful(testAccountList))

      val result = await(testService.getOrganisationsTeachers("org-test-org-id"))
      result mustBe testTeacherDetailsList
    }
  }

  "getOrganisationBasicDetails" should {
    "return org details" when {
      "given a valid org id" in new Setup {
        when(mockOrgAccountRepo.getOrgAccount[OrgDetails](ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testOrgDetails))

        val result = await(testService.getOrganisationBasicDetails("org-test-org-id"))
        result mustBe Some(testOrgDetails)
      }
    }

    "throw a MissingAccountException" in new Setup {
      when(mockOrgAccountRepo.getOrgAccount[OrgDetails](ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Future.failed(new MissingAccountException("")))

      val result = await(testService.getOrganisationBasicDetails("org-test-org-id"))
      result mustBe None
    }
  }
}
