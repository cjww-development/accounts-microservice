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

package services

import common.MissingAccountException
import helpers.services.ServiceSpec
import models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class OrgAccountServiceSpec extends ServiceSpec {

  class Setup {
    val testService = new OrgAccountService {
      override val orgAccountRepository  = mockOrgAccountRepo
      override val userAccountRepository = mockUserAccountRepo
    }
  }

  "getOrganisationsTeachers" should {
    "return a list of teacher details" in new Setup {
      mockGetOrgAccount(fetched = true)

      mockGetAllTeacherForOrg(found = true)

      awaitAndAssert(testService.getOrganisationsTeachers(generateTestSystemId(ORG))) {
        _ mustBe List(testTeacherDetails)
      }
    }
  }

  "getOrganisationBasicDetails" should {
    "return org details" when {
      "given a valid org id" in new Setup {
        when(mockOrgAccountRepo.getOrgAccount[OrgDetails](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(testOrgDetails))

        awaitAndAssert(testService.getOrganisationBasicDetails(generateTestSystemId(ORG))) {
          _ mustBe Some(testOrgDetails)
        }
      }
    }

    "throw a MissingAccountException" in new Setup {
      when(mockOrgAccountRepo.getOrgAccount[OrgDetails](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.failed(new MissingAccountException("")))

      awaitAndAssert(testService.getOrganisationBasicDetails(generateTestSystemId(ORG))) {
        _ mustBe None
      }
    }
  }
}
