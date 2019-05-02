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

import com.cjwwdev.mongo.responses.{MongoCreateResponse, MongoSuccessCreate}
import javax.inject.Inject
import models.{OrgAccount, UserAccount}
import auditing.Events.{individualReg, orgReg}
import play.api.mvc.Request
import repositories.{OrgAccountRepository, UserAccountRepository}

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultRegistrationService @Inject()(val userAccountRepository: UserAccountRepository,
                                           val orgAccountRepository: OrgAccountRepository,
                                           val messagingService: MessagingService) extends RegistrationService

trait RegistrationService {

  val messagingService: MessagingService

  val userAccountRepository: UserAccountRepository
  val orgAccountRepository: OrgAccountRepository

  def createNewUser(newUser: UserAccount)(implicit ec: ExC, req: Request[_]): Future[MongoCreateResponse] = {
    userAccountRepository.insertNewUser(newUser) map {
      case success@MongoSuccessCreate =>
        messagingService.sendAuditEvent(newUser.userId, individualReg.code, newUser.toAudit)
        success
    }
  }

  def createNewOrgUser(newOrgUser: OrgAccount)(implicit ec: ExC, req: Request[_]): Future[MongoCreateResponse] = {
    orgAccountRepository.insertNewOrgUser(newOrgUser) collect {
      case success@MongoSuccessCreate =>
        messagingService.sendAuditEvent(newOrgUser.orgId, orgReg.code, newOrgUser.toAudit)
        success
    }
  }
}
