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

import com.cjwwdev.logging.output.Logger
import javax.inject.Inject
import models.OrgAccount
import play.api.mvc.Request
import reactivemongo.bson.BSONDocument
import repositories.{OrgAccountRepository, UserAccountRepository}

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultAdminService @Inject()(val userAccountRepository: UserAccountRepository,
                                    val orgAccountRepository: OrgAccountRepository) extends AdminService

trait AdminService extends Logger {

  val userAccountRepository: UserAccountRepository
  val orgAccountRepository: OrgAccountRepository

  def getIdByUsername(userName: String)(implicit ec: ExC, req: Request[_]): Future[Option[String]] = {
    for {
      user <- userAccountRepository.getUserBySelector(BSONDocument("userName" -> userName)).map(Some(_)).recover { case _ => None }
      org  <- orgAccountRepository.getOrgAccount[OrgAccount](BSONDocument("orgUserName" -> userName)).map(Some(_)).recover { case _ => None }
    } yield {
      user -> org match {
        case (Some(acc), None) =>
          LogAt.info(s"[getIdByEmail] - Found Individual user based on user name")
          Some(acc.userId)
        case (None, Some(acc)) =>
          LogAt.info(s"[getIdByEmail] - Found Organisation user based on user name")
          Some(acc.orgId)
        case _                 =>
          LogAt.warn(s"[getIdByEmail] - Could not find user based on the supplied user name")
          None
      }
    }
  }
}
