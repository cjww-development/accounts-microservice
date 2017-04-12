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

import com.cjwwdev.mongo.{MongoConnector, MongoCreateResponse, MongoFailedRead, MongoSuccessRead}
import com.cjwwdev.logging.Logger
import com.google.inject.{Inject, Singleton}
import config.ApplicationConfiguration
import models.UserAccount
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait UserNameUse
case object UserNameInUse extends UserNameUse
case object UserNameNotInUse extends UserNameUse

sealed trait EmailUse
case object EmailInUse extends EmailUse
case object EmailNotInUse extends EmailUse

@Singleton
class RegistrationRepository @Inject()(mongoConnector : MongoConnector) extends ApplicationConfiguration {
  def insertNewUser(user : UserAccount) : Future[MongoCreateResponse] = {
    mongoConnector.create[UserAccount](USER_ACCOUNTS, user)
  }

  def verifyUserName(username : String) : Future[UserNameUse] = {
    mongoConnector.read[UserAccount](USER_ACCOUNTS, BSONDocument("userName" -> username)) map {
      case MongoSuccessRead(_)  =>
        Logger.info(s"[RegistrationRepository] - [verifyUserName] : This user name is already in use on this system")
        UserNameInUse
      case MongoFailedRead => UserNameNotInUse
    }
  }

  def verifyEmail(email : String) : Future[EmailUse] = {
    mongoConnector.read[UserAccount](USER_ACCOUNTS, BSONDocument("email" -> email)) map {
      case MongoSuccessRead(_) =>
        Logger.info(s"[RegistrationRepository] - [verifyEmail] : This email address is already in use on this system")
        EmailInUse
      case MongoFailedRead => EmailNotInUse
    }
  }
}
