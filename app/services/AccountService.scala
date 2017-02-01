// Copyright (C) 2011-2012 the original author or authors.
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

import config.{MongoFailedUpdate, MongoSuccessUpdate}
import models.{AccountSettings, UpdatedPassword, UserProfile}
import repositories.AccountDetailsRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait UpdatedPasswordResponse
case object InvalidOldPassword extends UpdatedPasswordResponse
case class PasswordUpdate(success : Boolean) extends UpdatedPasswordResponse

sealed trait UpdatedSettingsResponse
case object UpdatedSettingsSuccess extends UpdatedSettingsResponse
case object UpdatedSettingsFailed extends UpdatedSettingsResponse

object AccountService extends AccountService {
  val accountDetailsRepo = AccountDetailsRepository
}

trait AccountService {

  val accountDetailsRepo : AccountDetailsRepository

  def updateProfileInformation(userProfile: UserProfile) : Future[Boolean] = {
    accountDetailsRepo.updateAccountData(userProfile) map {
      case MongoFailedUpdate => true
      case MongoSuccessUpdate => false
    }
  }

  def updatePassword(passwordSet : UpdatedPassword) : Future[UpdatedPasswordResponse] = {
    accountDetailsRepo.findPassword(passwordSet) flatMap {
      case false => Future.successful(InvalidOldPassword)
      case true => accountDetailsRepo.updatePassword(passwordSet) map {
        case MongoFailedUpdate => PasswordUpdate(true)
        case MongoSuccessUpdate => PasswordUpdate(false)
      }
    }
  }

  def updateSettings(accountSettings : AccountSettings) : Future[UpdatedSettingsResponse] = {
    accountDetailsRepo.updateSettings(accountSettings) map {
      case MongoFailedUpdate => UpdatedSettingsFailed
      case MongoSuccessUpdate => UpdatedSettingsSuccess
    }
  }
}