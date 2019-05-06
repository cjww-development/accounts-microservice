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

import java.time.LocalDateTime

import auditing.Events._
import com.cjwwdev.mongo.responses.MongoUpdatedResponse
import common._
import javax.inject.Inject
import models.{Settings, UpdatedPassword, UserProfile}
import play.api.libs.json.Json
import play.api.mvc.Request
import reactivemongo.bson.BSONDocument
import repositories.UserAccountRepository

import scala.concurrent.{Future, ExecutionContext => ExC}

class DefaultAccountService @Inject()(val userAccountRepository: UserAccountRepository,
                                      val messagingService: MessagingService) extends AccountService

trait AccountService {

  val userAccountRepository: UserAccountRepository

  val messagingService: MessagingService

  def updateProfileInformation(userId: String, userProfile: UserProfile)(implicit ec: ExC, req: Request[_]): Future[MongoUpdatedResponse] = {
    for {
      account <- userAccountRepository.getUserBySelector(BSONDocument("userId" -> userId))
      _ = messagingService.sendAuditEvent(account.userId, indDetailsUpdate.code, Map(
        "previousDetails" -> account.toDetailsAudit,
        "updatedDetails"  -> Map(
          "firstName" -> userProfile.firstName,
          "lastName"  -> userProfile.lastName,
          "email"     -> userProfile.email
        )
      ))
      updated <- userAccountRepository.updateAccountData(account.userId, userProfile)
    } yield updated
  }

  def updatePassword(userId: String, passwordSet: UpdatedPassword)(implicit ec: ExC, req: Request[_]): Future[UpdatedPasswordResponse] = {
    userAccountRepository.findPassword(userId, passwordSet.previousPassword) flatMap { _ =>
      userAccountRepository.updatePassword(userId, passwordSet.newPassword) map { _ =>
        messagingService.sendAuditEvent(userId, indPasswordUpdate.code, Map(
          "updated" -> LocalDateTime.now()
        ))
        PasswordUpdated
      } recover {
        case _ => PasswordUpdateFailed
      }
    } recover {
      case _ => InvalidOldPassword
    }
  }

  def updateSettings(userId: String, accountSettings: Settings)(implicit ec: ExC, req: Request[_]): Future[UpdatedSettingsResponse] = {
    for {
      account <- userAccountRepository.getUserBySelector(BSONDocument("userId" -> userId))
      _ = messagingService.sendAuditEvent(account.userId, indSettingsUpdate.code, Map(
        "previousSettings" -> Json.toJson(account.settings.getOrElse(Settings("-", "-", "-"))),
        "newSettings"      -> Json.toJson(accountSettings)
      ))
      updated <- userAccountRepository.updateSettings(account.userId, accountSettings) map {
        _ => UpdatedSettingsSuccess
      } recover {
        case _ => UpdatedSettingsFailed
      }
    } yield updated
  }
}
