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

import javax.inject.{Inject, Singleton}

import com.cjwwdev.reactivemongo.{MongoFailedUpdate, MongoSuccessUpdate, MongoUpdatedResponse}
import models.{AccountSettings, UpdatedPassword, UserProfile}
import repositories.{UserAccountRepo, UserAccountRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait UpdatedPasswordResponse
case object InvalidOldPassword extends UpdatedPasswordResponse
case class PasswordUpdate(success : Boolean) extends UpdatedPasswordResponse

sealed trait UpdatedSettingsResponse
case object UpdatedSettingsSuccess extends UpdatedSettingsResponse
case object UpdatedSettingsFailed extends UpdatedSettingsResponse

@Singleton
class AccountService @Inject()(userAccountRepository: UserAccountRepository) {

  val userAccountStore: UserAccountRepo = userAccountRepository.store

  def updateProfileInformation(userId: String, userProfile: UserProfile) : Future[MongoUpdatedResponse] = {
    userAccountStore.updateAccountData(userId, userProfile)
  }

  def updatePassword(userId: String, passwordSet : UpdatedPassword) : Future[UpdatedPasswordResponse] = {
    userAccountStore.findPassword(userId, passwordSet) flatMap {
      case false  => Future.successful(InvalidOldPassword)
      case true   => userAccountStore.updatePassword(userId, passwordSet) map {
        case MongoSuccessUpdate => PasswordUpdate(true)
        case MongoFailedUpdate  => PasswordUpdate(false)
      }
    }
  }

  def updateSettings(userId: String, accountSettings : AccountSettings) : Future[UpdatedSettingsResponse] = {
    userAccountStore.updateSettings(userId, accountSettings) map {
      case MongoFailedUpdate    => UpdatedSettingsFailed
      case MongoSuccessUpdate   => UpdatedSettingsSuccess
    }
  }
}
