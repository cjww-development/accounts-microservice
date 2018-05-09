/*
 * Copyright 2018 CJWW Development
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
package repositories

import com.cjwwdev.logging.Logging
import com.cjwwdev.mongo.DatabaseRepository
import com.cjwwdev.mongo.connection.ConnectionSettings
import com.cjwwdev.mongo.responses._
import common.{FailedToCreateException, FailedToUpdateException, MissingAccountException, _}
import javax.inject.Inject
import models._
import play.api.Configuration
import play.api.libs.json.OFormat
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserAccountRepositoryImpl @Inject()(val config: Configuration) extends UserAccountRepository with ConnectionSettings

trait UserAccountRepository extends DatabaseRepository with Logging {
  override def indexes: Seq[Index] = Seq(
    Index(
      key     = Seq("userId" -> IndexType.Ascending),
      name    = Some("UserId"),
      unique  = true,
      sparse  = false
    )
  )

  def insertNewUser(user : UserAccount): Future[MongoCreateResponse] = {
    for {
      col <- collection
      wr  <- col.insert[UserAccount](user)
    } yield if(wr.ok) MongoSuccessCreate else throw new FailedToCreateException(s"Failed to create new UserAccount")
  }

  def getUserBySelector(selector: BSONDocument): Future[UserAccount] = {
    for {
      col           <- collection
      (name, value) =  getSelectorHead(selector)
      acc           <- col.find(selector).one[UserAccount]
    } yield acc.getOrElse({
      logger.error(s"[UserAccountRepository] - [getUserBySelector] - Could not find user account based on $name with value $value")
      throw new MissingAccountException(s"No user account found based on $name with value $value")
    })
  }

  def verifyUserName(username : String): Future[UserNameUse] = {
    for {
      col <- collection
      acc <- col.find(userNameSelector(username)).one[UserAccount]
    } yield if(acc.isDefined) {
      logger.info(s"[UserAccountRepo] - [verifyUserName] : This user name is already in use on this system")
      UserNameInUse
    } else {
      UserNameNotInUse
    }
  }

  def verifyEmail(email : String): Future[EmailUse] = {
    for {
      col <- collection
      acc <- col.find(userEmailSelector(email)).one[UserAccount]
    } yield if(acc.isDefined) {
      logger.info(s"[UserAccountRepo] - [verifyEmail] : This email address is already in use on this system")
      EmailInUse
    } else {
      EmailNotInUse
    }
  }

  def updateAccountData(userId: String, userProfile: UserProfile)(implicit format: OFormat[UserProfile]): Future[MongoUpdatedResponse] = {
    for {
      col         <- collection
      updatedData =  BSONDocument("$set" -> BSONDocument(
        "firstName" -> userProfile.firstName,
        "lastName"  -> userProfile.lastName,
        "email"     -> userProfile.email
      ))
      uwr         <- col.update(userIdSelector(userId), updatedData)
    } yield if(uwr.ok) MongoSuccessUpdate else throw new FailedToUpdateException(s"Failed to update user profile for user id $userId")
  }

  def findPassword(userId: String, oldPassword: String)(implicit format: OFormat[UpdatedPassword]): Future[Boolean] = {
    for {
      col <- collection
      acc <- col.find(userIdPasswordSelector(userId, oldPassword)).one[UserAccount]
    } yield acc.fold(throw new MissingAccountException(s"No matching user account for user id $userId"))(_ => true)
  }

  def updatePassword(userId: String, newPassword : String)(implicit format: OFormat[UpdatedPassword]): Future[MongoUpdatedResponse] = {
    for {
      col    <- collection
      update = BSONDocument("$set" -> BSONDocument("password" -> newPassword))
      uwr    <- col.update(userIdSelector(userId), update)
    } yield if(uwr.ok) MongoSuccessUpdate else throw new FailedToUpdateException(s"Failed to update password for user id $userId")
  }

  def updateSettings(userId: String, newSettings : Settings): Future[MongoUpdatedResponse] = {
    for {
      col    <- collection
      update =  BSONDocument("$set" -> BSONDocument("settings" -> BSONDocument(
        "displayName"       -> newSettings.displayName,
        "displayNameColour" -> newSettings.displayNameColour,
        "displayImageURL"   -> newSettings.displayImageURL
      )))
      uwr    <- col.update(userIdSelector(userId), update)
    } yield if(uwr.ok) MongoSuccessUpdate else throw new FailedToUpdateException(s"Failed to update user settings for user id $userId")
  }

  def getAllTeacherForOrg(orgName: String): Future[List[UserAccount]] = {
    for {
      col  <- collection
      list <- col.find(deversitySchoolSelector(orgName)).cursor[UserAccount]().collect[List]()
    } yield list
  }

  def deleteUserAccount(userId: String): Future[MongoDeleteResponse] = {
    for {
      col <- collection
      wr  <- col.remove(userIdSelector(userId))
    } yield if(wr.ok) MongoSuccessDelete else MongoFailedDelete
  }
}
