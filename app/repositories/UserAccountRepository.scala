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

import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.cjwwdev.reactivemongo._
import config.{FailedToCreateException, FailedToUpdateException, MissingAccountException}
import config._
import models._
import play.api.Logger
import play.api.libs.json.{JsObject, Json, OFormat}
import reactivemongo.api.Cursor
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.core.server.NettyServer

@Singleton
class UserAccountRepository @Inject()() extends MongoDatabase("user-accounts") {
  override def indexes: Seq[Index] = Seq(
    Index(
      key     = Seq("userId" -> IndexType.Ascending),
      name    = Some("UserId"),
      unique  = true,
      sparse  = false
    )
  )

  private def userIdSelector(userId: String): BSONDocument = BSONDocument("userId" -> userId)
  private def deversitySchoolSelector(orgName: String): BSONDocument = BSONDocument(
    "deversityDetails.schoolName" -> orgName,
    "deversityDetails.role" -> "teacher"
  )
  private def generateDeversityId: String = s"deversity-${UUID.randomUUID()}"

  def insertNewUser(user : UserAccount) : Future[MongoCreateResponse] = {
    collection flatMap {
      _.insert(user) map { wr =>
        if(wr.ok) MongoSuccessCreate else throw new FailedToCreateException(s"Failed to create new UserAccount")
      }
    }
  }

  def verifyUserName(username : String) : Future[UserNameUse] = {
    collection flatMap {
      _.find(BSONDocument("userName" -> username)).one[UserAccount] map {
        case Some(_)  =>
          Logger.info(s"[UserAccountRepo] - [verifyUserName] : This user name is already in use on this system")
          UserNameInUse
        case None     => UserNameNotInUse
      }
    }
  }

  def verifyEmail(email : String) : Future[EmailUse] = {
    collection flatMap {
      _.find(BSONDocument("email" -> email)).one[UserAccount] map {
        case Some(_)  =>
          Logger.info(s"[UserAccountRepo] - [verifyEmail] : This email address is already in use on this system")
          EmailInUse
        case None     => EmailNotInUse
      }
    }
  }

  def getAccount(userId : String) : Future[UserAccount] = {
    collection flatMap {
      _.find(userIdSelector(userId)).one[UserAccount] map {
        case Some(acc)  => acc
        case None       => throw new MissingAccountException(s"No user account found for user id $userId")
      }
    }
  }

  def updateAccountData(userId: String, userProfile: UserProfile)(implicit format: OFormat[UserProfile]) : Future[MongoUpdatedResponse] = {
    val updatedData = BSONDocument("$set" -> BSONDocument(
      "firstName" -> userProfile.firstName,
      "lastName"  -> userProfile.lastName,
      "email"     -> userProfile.email
    ))
    collection.flatMap {
      _.update(userIdSelector(userId), updatedData) map { wr =>
        if(wr.ok) MongoSuccessUpdate else throw new FailedToUpdateException(s"Failed to update user profile for user id $userId")
      }
    }
  }

  def findPassword(userId: String, oldPassword: String)(implicit format: OFormat[UpdatedPassword]) : Future[Boolean] = {
    collection flatMap {
      _.find(BSONDocument("userId" -> userId, "password" -> oldPassword)).one[UserAccount] map {
        case Some(_) => true
        case None    => throw new MissingAccountException(s"No matching user account for user id $userId")
      }
    }
  }

  def updatePassword(userId: String, newPassword : String)(implicit format: OFormat[UpdatedPassword]) : Future[MongoUpdatedResponse] = {
    collection flatMap {
      _.update(userIdSelector(userId), BSONDocument("$set" -> BSONDocument("password" -> newPassword))) map { wr =>
        if(wr.ok) MongoSuccessUpdate else throw new FailedToUpdateException(s"Failed to update password for user id $userId")
      }
    }
  }

  def updateSettings(userId: String, newSettings : Settings): Future[MongoUpdatedResponse] = {
    val settings = BSONDocument("$set" -> BSONDocument("settings" -> BSONDocument(
      "displayName"       -> newSettings.displayName,
      "displayNameColour" -> newSettings.displayNameColour,
      "displayImageURL"   -> newSettings.displayImageURL
    )))

    collection flatMap {
      _.update(userIdSelector(userId), settings) map { wr =>
        if(wr.ok) MongoSuccessUpdate else throw new FailedToUpdateException(s"Failed to update user settings for user id $userId")
      }
    }
  }

  def getAllTeacherForOrg(orgName: String): Future[List[UserAccount]] = {
    collection flatMap {
      _.find(deversitySchoolSelector(orgName)).cursor[UserAccount]().collect[List]()
    }
  }
}
