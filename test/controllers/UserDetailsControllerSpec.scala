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
package controllers

import java.util.UUID

import helpers.CJWWSpec
import mocks.AuthBuilder
import models.{BasicDetails, Enrolments, OrgDetails, Settings}
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers
import play.api.test.Helpers._

import scala.concurrent.Future

class UserDetailsControllerSpec extends CJWWSpec {

  final val now = new DateTime(DateTimeZone.UTC)

  final val uuid = UUID.randomUUID

  val testBasicDetails = BasicDetails(
    "testFirstName",
    "testLastName",
    "testUserName",
    "test@email.com",
    now
  )

  val testEnrolments = Enrolments(
    Some("testId"),
    Some("testId"),
    Some("testId")
  )

  val testSettings = Settings(
    displayName = "full",
    displayNameColour = "#FFFFFF",
    displayImageURL  = "/test/uri"
  )

  val testOrgDetails = OrgDetails(
    orgName = "testOrgName",
    initials = "TI",
    location = "testLocation"
  )

  lazy val request = buildRequest

  class Setup {
    val testController = new UserDetailsController(mockGetDetailsService, mockOrgAccountService, mockAuthConnector)
  }

  "getBasicDetails" should {
    "return an ok" when {
      "the users basic details have been found" in new Setup {
        when(mockGetDetailsService.getBasicDetails(ArgumentMatchers.any()))
          .thenReturn(Future.successful(testBasicDetails))

        AuthBuilder.getWithAuthorisedUser(testController.getBasicDetails(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe OK
        }
      }
    }
  }

  "getEnrolments" should {
    "return an ok" when {
      "the users enrolments have been found" in new Setup {
        when(mockGetDetailsService.getEnrolments(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testEnrolments)))

        AuthBuilder.getWithAuthorisedUser(testController.getEnrolments(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe OK
        }
      }
    }

    "return a not found" when {
      "no enrolments have been found" in new Setup {
        when(mockGetDetailsService.getEnrolments(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        AuthBuilder.getWithAuthorisedUser(testController.getEnrolments(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe NOT_FOUND
        }
      }
    }
  }

  "getSettings" should {
    "return an ok" when {
      "the users settings have been found" in new Setup {
        when(mockGetDetailsService.getSettings(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testSettings)))

        AuthBuilder.getWithAuthorisedUser(testController.getSettings(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe OK
        }
      }
    }

    "return a not found" when {
      "no settings have been found" in new Setup {
        when(mockGetDetailsService.getSettings(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        AuthBuilder.getWithAuthorisedUser(testController.getSettings(s"user-$uuid"), request, mockAuthConnector, uuid, "user") {
          result => status(result) mustBe NOT_FOUND
        }
      }
    }
  }

  "getOrgBasicDetails" should {
    "return an OK" when {
      "an orgs basic details are found" in new Setup {
        when(mockOrgAccountService.getOrganisationBasicDetails(ArgumentMatchers.any()))
          .thenReturn(Future.successful(Some(testOrgDetails)))

        AuthBuilder.getWithAuthorisedUser(testController.getOrgBasicDetails(s"org-user-$uuid"), request, mockAuthConnector, uuid, "org") {
          result => status(result) mustBe OK
        }
      }
    }

    "return a not found" when {
      "no basic details are found for an org" in new Setup {
        when(mockOrgAccountService.getOrganisationBasicDetails(ArgumentMatchers.any()))
          .thenReturn(Future.successful(None))

        AuthBuilder.getWithAuthorisedUser(testController.getOrgBasicDetails(s"org-user-$uuid"), request, mockAuthConnector, uuid, "org") {
          result => status(result) mustBe NOT_FOUND
        }
      }
    }
  }
}