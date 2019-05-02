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

package services

import common.MissingAccountException
import javax.inject.Inject
import models.{OrgAccount, OrgDetails, TeacherDetails}
import reactivemongo.bson.BSONDocument
import repositories.{OrgAccountRepository, UserAccountRepository}

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultOrgAccountService @Inject()(val orgAccountRepository: OrgAccountRepository,
                                         val userAccountRepository: UserAccountRepository) extends OrgAccountService

trait OrgAccountService {
  val orgAccountRepository: OrgAccountRepository
  val userAccountRepository: UserAccountRepository

  private val orgIdSelector: String => BSONDocument = orgId => BSONDocument("orgId" -> orgId)

  def getOrganisationBasicDetails(orgId: String)(implicit ec: ExC): Future[Option[OrgDetails]] = {
    orgAccountRepository.getOrgAccount[OrgDetails](orgIdSelector(orgId)) map {
      Some(_)
    } recover {
      case _: MissingAccountException => None
    }
  }

  def getOrganisationsTeachers(orgId: String)(implicit ec: ExC): Future[List[TeacherDetails]] = {
    for {
      orgAcc    <- orgAccountRepository.getOrgAccount[OrgAccount](orgIdSelector(orgId))
      teachers  <- userAccountRepository.getAllTeacherForOrg(orgAcc.deversityId)
    } yield teachers map { user =>
      TeacherDetails(
        user.userId,
        user.deversityDetails.get.title.get,
        user.lastName,
        user.deversityDetails.get.room.get
      )
    }
  }
}
