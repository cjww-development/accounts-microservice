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

import javax.inject.Inject

import common.MissingAccountException
import models.{OrgAccount, OrgDetails, TeacherDetails}
import repositories.{OrgAccountRepository, UserAccountRepository}
import selectors.OrgAccountSelectors._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrgAccountServiceImpl @Inject()(val orgAccountRepository: OrgAccountRepository,
                                      val userAccountRepository: UserAccountRepository) extends OrgAccountService

trait OrgAccountService {
  val orgAccountRepository: OrgAccountRepository
  val userAccountRepository: UserAccountRepository

  def getOrganisationBasicDetails(orgId: String): Future[Option[OrgDetails]] = {
    orgAccountRepository.getOrgAccount[OrgDetails](orgIdSelector(orgId)) map {
      details => Some(details)
    } recover {
      case _: MissingAccountException => None
    }
  }

  def getOrganisationsTeachers(orgId: String): Future[List[TeacherDetails]] = {
    for {
      orgAcc    <- orgAccountRepository.getOrgAccount[OrgAccount](orgIdSelector(orgId))
      teachers  <- userAccountRepository.getAllTeacherForOrg(orgAcc.orgUserName)
    } yield teachers map { user =>
      TeacherDetails(
        user.userId,
        user.deversityDetails.get.title.get,
        user.lastName,
        user.deversityDetails.get.room.get,
        user.deversityDetails.get.statusConfirmed
      )
    }
  }
}
