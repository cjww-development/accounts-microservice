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

package repositories

import javax.inject.{Inject, Singleton}

import com.cjwwdev.mongo._
import config.ApplicationConfiguration
import models.{AccountSettings, UpdatedPassword, UserAccount, UserProfile}
import play.api.libs.json._
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AccountDetailsRepository @Inject()(mongoConnector: MongoConnector) extends ApplicationConfiguration {
  def updateAccountData(userId: String, userProfile: UserProfile)(implicit format: OFormat[UserProfile]) : Future[MongoUpdatedResponse] = {
    val selector = BSONDocument("_id" -> userId)
    mongoConnector.read[UserAccount](USER_ACCOUNTS, selector) flatMap {
      case MongoSuccessRead(profile) =>
        val updatedData = profile.asInstanceOf[UserAccount].copy(
          firstName = userProfile.firstName,
          lastName = userProfile.lastName,
          email = userProfile.email
        )
        mongoConnector.update(USER_ACCOUNTS, selector, updatedData)
      case _ => Future.successful(MongoFailedUpdate)
    }
  }

  def findPassword(userId: String, passwordSet : UpdatedPassword)(implicit format: OFormat[UpdatedPassword]) : Future[Boolean] = {
    mongoConnector.read[UserAccount](USER_ACCOUNTS, BSONDocument("_id" -> userId, "password" -> passwordSet.previousPassword)) map {
      case MongoSuccessRead(_) => true
      case MongoFailedRead => false
    }
  }

  def updatePassword(userId: String, passwordSet : UpdatedPassword)(implicit format: OFormat[UpdatedPassword]) : Future[MongoUpdatedResponse] = {
    val selector = BSONDocument("_id" -> userId)
    mongoConnector.read[UserAccount](USER_ACCOUNTS, selector) flatMap {
      case MongoSuccessRead(profile) =>
        val updatedData = profile.asInstanceOf[UserAccount].copy(
          password = passwordSet.newPassword
        )
        mongoConnector.update(USER_ACCOUNTS, selector, updatedData)
      case _ => Future.successful(MongoFailedUpdate)
    }
  }

  def updateSettings(userId: String, accSettings : AccountSettings)(implicit format: OFormat[AccountSettings]) : Future[MongoUpdatedResponse] = {
    val selector = BSONDocument("_id" -> userId)
    mongoConnector.read[UserAccount](USER_ACCOUNTS, selector) flatMap {
      case MongoSuccessRead(profile) =>
        val updatedData = profile.asInstanceOf[UserAccount].copy(
          settings = Some(Map(
            "displayName" -> accSettings.settings("displayName"),
            "displayNameColour" -> accSettings.settings("displayNameColour"),
            "displayImageURL" -> accSettings.settings("displayImageURL")
          ))
        )
        mongoConnector.update(USER_ACCOUNTS, selector, updatedData)
      case _ => Future.successful(MongoFailedUpdate)
    }
  }
}
