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

import com.cjwwdev.mongo.responses.MongoUpdatedResponse
import common._
import javax.inject.Inject
import models.{Settings, UpdatedPassword, UserProfile}
import repositories.UserAccountRepository

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultAccountService @Inject()(val userAccountRepository: UserAccountRepository) extends AccountService

trait AccountService {
  val userAccountRepository: UserAccountRepository

  def updateProfileInformation(userId: String, userProfile: UserProfile)(implicit ec: ExC): Future[MongoUpdatedResponse] = {
    userAccountRepository.updateAccountData(userId, userProfile)
  }

  def updatePassword(userId: String, passwordSet: UpdatedPassword)(implicit ec: ExC): Future[UpdatedPasswordResponse] = {
    userAccountRepository.findPassword(userId, passwordSet.previousPassword) flatMap { _ =>
      userAccountRepository.updatePassword(userId, passwordSet.newPassword) map {
        _ => PasswordUpdated
      } recover {
        case _ => PasswordUpdateFailed
      }
    } recover {
      case _ => InvalidOldPassword
    }
  }

  def updateSettings(userId: String, accountSettings: Settings)(implicit ec: ExC): Future[UpdatedSettingsResponse] = {
    userAccountRepository.updateSettings(userId, accountSettings) map {
      _ => UpdatedSettingsSuccess
    } recover {
      case _ => UpdatedSettingsFailed
    }
  }
}
