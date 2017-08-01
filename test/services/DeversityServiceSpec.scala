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

import com.cjwwdev.reactivemongo.MongoSuccessUpdate
import com.cjwwdev.security.encryption.DataSecurity
import config.{FailedToUpdateException, MissingAccountException}
import helpers.CJWWSpec
import fixtures.AccountFixtures._
import models.{OrgDetails, TeacherDetails}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class DeversityServiceSpec extends CJWWSpec {

  class Setup {
    val testService = new DeversityService(mockUserAccountRepo, mockOrgAccountRepo)

  }

  "findSchool" should {
    "return true" when {
      "a matching school is found" in new Setup {
        when(mockOrgAccountRepo.findSchool(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testOrgAccount))

        val result = await(testService.findSchool("testOrgName"))
        result mustBe true
      }
    }

    "return false" when {
      "no matching school was found" in new Setup {
        when(mockOrgAccountRepo.findSchool(ArgumentMatchers.any()))
          .thenReturn(Future.failed(new MissingAccountException("No org account found for org name testOrgName")))

        val result = await(testService.findSchool("testOrgName"))
        result mustBe false
      }
    }
  }

  "findTeacher" should {
    "return true" when {
      "a matching teacher was found" in new Setup {
        when(mockUserAccountRepo.findTeacher(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(testAccount))

        val result = await(testService.findTeacher("testUserName", "testSchoolName"))
        result mustBe true
      }
    }

    "return a false" when {
      "no matching teacher was found" in new Setup {
        when(mockUserAccountRepo.findTeacher(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.failed(new MissingAccountException("No user account found matching teacher name testUserName and school name testSchoolName")))

        val result = await(testService.findTeacher("testUserName", "testSchoolName"))
        result mustBe false
      }
    }
  }

  "getTeacherDetails" should {
    val testTeacherDetails = TeacherDetails("Prof", "testLastName", "testRoom", "pending")

    "return some teacher details" when {
      "a matching teacher is found" in new Setup {
        when(mockUserAccountRepo.findTeacher(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(testAccount))

        val result = await(testService.getTeacherDetails("testUserName", "testSchoolName"))
        result mustBe Some(testTeacherDetails)
      }
    }

    "return none" when {
      "no matching teacher was found" in new Setup {
        when(mockUserAccountRepo.findTeacher(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(testAccount.copy(deversityDetails = None)))

        val result = await(testService.getTeacherDetails("testUserName", "testSchoolName"))
        result mustBe None
      }

      "there was no matching account" in new Setup {
        when(mockUserAccountRepo.findTeacher(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.failed(new MissingAccountException("No user account found matching teacher name teacherName and school name schoolName")))

        val result = await(testService.getTeacherDetails("testUserName", "testSchoolName"))
        result mustBe None
      }
    }
  }

  "getSchoolDetails" should {
    val testOrgDetails = OrgDetails("testOrgName", "TI", "testLocation")

    "return some school details" when {
      "a matching a school was found" in new Setup {
        when(mockOrgAccountRepo.getSchoolDetails(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testOrgDetails))

        val result = await(testService.getSchoolDetails("testOrgName"))
        result mustBe Some(testOrgDetails)
      }
    }

    "return none" when {
      "no matching school was found" in new Setup {
        when(mockOrgAccountRepo.getSchoolDetails(ArgumentMatchers.any()))
          .thenReturn(Future.failed(new MissingAccountException("No org account found for org name $orgName")))

        val result = await(testService.getSchoolDetails("testOrgName"))
        result mustBe None
      }
    }
  }

  "getDeversityUserInformation" should {
    "return some deversity enrolment" when {
      "an active deversity user has been found" in new Setup {
        when(mockUserAccountRepo.getAccount(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testAccount))

        val result = await(testService.getDeversityUserInformation("testId"))
        result mustBe testAccount.deversityDetails
      }
    }

    "return none" when {
      "no matching deversity user has been found" in new Setup {
        when(mockUserAccountRepo.getAccount(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testAccount.copy(deversityDetails = None)))

        val result = await(testService.getDeversityUserInformation("testId"))
        result mustBe None
      }
    }
  }

  "updateDeversityUserInformation" should {
    "update the users deversity information" in new Setup {
      when(mockUserAccountRepo.updateDeversityDataBlock(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(MongoSuccessUpdate))

      val result = await(testService.updateDeversityUserInformation("testId", testAccount.deversityDetails.get))
      result mustBe MongoSuccessUpdate
    }

    "throw a FailedToUpdateException" in new Setup {
      when(mockUserAccountRepo.updateDeversityDataBlock(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.failed(new FailedToUpdateException("There was a problem updating the deversity enrolment block for $userId")))

      intercept[FailedToUpdateException](await(testService.updateDeversityUserInformation("testId", testAccount.deversityDetails.get)))
    }
  }

  "createOrUpdateEnrolments" should {
    "return an encrypted string" in new Setup {
      when(mockUserAccountRepo.updateDeversityEnrolment(ArgumentMatchers.any()))
        .thenReturn(Future.successful("deversity-test-id"))

      val result = await(testService.createOrUpdateEnrolments("testId"))
      DataSecurity.decryptString(result).contains("deversity-test-id")
    }

    "throw a FailedToUpdateException" in new Setup {
      when(mockUserAccountRepo.updateDeversityEnrolment(ArgumentMatchers.any()))
        .thenReturn(Future.failed(new FailedToUpdateException("Failed to update dev id for user $userId")))

      intercept[FailedToUpdateException](await(testService.createOrUpdateEnrolments("testId")))
    }
  }
}
