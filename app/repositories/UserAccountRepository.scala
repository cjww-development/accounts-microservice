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

import com.cjwwdev.logging.Logger
import com.cjwwdev.reactivemongo._
import config.Exceptions.{FailedToCreateException, FailedToUpdateException, MissingAccountException}
import config._
import models._
import play.api.libs.json.OFormat
import reactivemongo.api.DB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserAccountRepository @Inject()() extends MongoConnector {
  val store = new UserAccountRepo(db)
}

class UserAccountRepo(db: () => DB) extends MongoRepository("user-accounts", db) {

  override def indexes: Seq[Index] = Seq(
    Index(
      key = Seq("userId" -> IndexType.Ascending),
      name = Some("UserId"),
      unique = true,
      sparse = false
    )
  )

  private def userIdSelector(userId: String): BSONDocument = BSONDocument("userId" -> userId)
  private def deversitySchoolSelector(orgName: String): BSONDocument = BSONDocument(
    "deversityDetails.schoolName" -> orgName,
    "deversityDetails.role" -> "teacher"
  )
  private def generateDeversityId: String = s"deversity-${UUID.randomUUID()}"

  def insertNewUser(user : UserAccount) : Future[MongoCreateResponse] = {
    collection.insert(user) map { writeResult =>
      if(writeResult.ok) MongoSuccessCreate else throw new FailedToCreateException("Failed to create new UserAccount")
    }
  }

  def verifyUserName(username : String) : Future[UserNameUse] = {
    collection.find(BSONDocument("userName" -> username)).one[UserAccount] map {
      case Some(_)  =>
        Logger.info(s"[UserAccountRepo] - [verifyUserName] : This user name is already in use on this system")
        UserNameInUse
      case None     => UserNameNotInUse
    }
  }

  def verifyEmail(email : String) : Future[EmailUse] = {
    collection.find(BSONDocument("email" -> email)).one[UserAccount] map {
      case Some(_)  =>
        Logger.info(s"[UserAccountRepo] - [verifyEmail] : This email address is already in use on this system")
        EmailInUse
      case None     => EmailNotInUse
    }
  }

  def getAccount(userId : String) : Future[UserAccount] = {
    collection.find(userIdSelector(userId)).one[UserAccount] map {
      case Some(acc)  => acc
      case None       => throw new MissingAccountException(s"No user account found for user id $userId")
    }
  }

  def updateAccountData(userId: String, userProfile: UserProfile)(implicit format: OFormat[UserProfile]) : Future[MongoUpdatedResponse] = {
    collection.find(userIdSelector(userId)).one[UserAccount] flatMap {
      case Some(acc) =>
        val updatedData = acc.copy(
          firstName = userProfile.firstName,
          lastName  = userProfile.lastName,
          email     = userProfile.email
        )
        collection.update(userIdSelector(userId),updatedData) map { writeResult =>
          if(writeResult.ok) {
            MongoSuccessUpdate
          } else {
            throw new FailedToUpdateException(s"Failed to update user profile for user id $userId")
          }
        }
      case None => throw new MissingAccountException(s"No user account found for user id $userId")
    }
  }

  def findPassword(userId: String, passwordSet : UpdatedPassword)(implicit format: OFormat[UpdatedPassword]) : Future[Boolean] = {
    collection.find(BSONDocument("_id" -> userId, "password" -> passwordSet.previousPassword)).one[UserAccount] map {
      case Some(_)  => true
      case None     => throw new MissingAccountException(s"No user account found for user id $userId")
    }
  }

  def updatePassword(userId: String, passwordSet : UpdatedPassword)(implicit format: OFormat[UpdatedPassword]) : Future[MongoUpdatedResponse] = {
    collection.find(userIdSelector(userId)).one[UserAccount] flatMap {
      case Some(acc) =>
        val updatedData = acc.copy(password = passwordSet.newPassword)
        collection.update(userIdSelector(userId), updatedData) map { writeResult =>
          if(writeResult.ok) {
            MongoSuccessUpdate
          } else {
            throw new FailedToUpdateException(s"Failed to update password for user id $userId")
          }
        }
      case None => throw new MissingAccountException(s"No user account found for user id $userId")
    }
  }

  def updateSettings(userId: String, newSettings : Settings): Future[MongoUpdatedResponse] = {
    collection.find(userIdSelector(userId)).one[UserAccount] flatMap {
      case Some(acc) =>
        val updatedData = acc.copy(settings = Some(newSettings))
        collection.update(userIdSelector(userId), updatedData) map { writeResult =>
          if(writeResult.ok) {
            MongoSuccessUpdate
          } else {
            throw new FailedToUpdateException(s"Failed to update user settings for user id $userId")
          }
        }
      case None => throw new MissingAccountException(s"No user account for user id $userId")
    }
  }

  def getDeversityUserDetails(userId: String): Future[UserAccount] = {
    collection.find(userIdSelector(userId)).one[UserAccount] map {
      case Some(acc)  => acc
      case None       => throw new MissingAccountException(s"No user account found for $userId")
    }
  }

  def updateDeversityEnrolment(userId: String): Future[String] = {
    val generatedDevId = generateDeversityId
    collection.find(userIdSelector(userId)).one[UserAccount] flatMap {
      case Some(acc) => acc.enrolments match {
        case Some(enr) => collection.update(userIdSelector(userId), acc.copy(enrolments = Some(enr.copy(deversityId = Some(generatedDevId))))) map {
          writeResult =>
            if(writeResult.ok) {
              generatedDevId
            } else {
              throw new FailedToUpdateException(s"Failed to update dev id for user $userId")
            }
        }
        case None => collection.update(userIdSelector(userId), acc.copy(enrolments = Some(Enrolments(None, None, Some(generatedDevId))))) map { writeResult =>
          if(writeResult.ok) {
            generatedDevId
          } else {
            throw new FailedToUpdateException(s"Failed to update dev id for user $userId")
          }
        }
      }
      case None => throw new MissingAccountException(s"No user account found for $userId")
    }
  }

  def updateDeversityDataBlock(userId: String, deversityEnrolmentDetails: DeversityEnrolment): Future[MongoUpdatedResponse] = {
    collection.find(userIdSelector(userId)).one[UserAccount] flatMap {
      case Some(acc) => collection.update(userIdSelector(userId), acc.copy(deversityDetails = Some(deversityEnrolmentDetails))) map {
        writeResult =>
          if(writeResult.ok) {
            MongoSuccessUpdate
          } else {
            throw new FailedToUpdateException(s"There was a problem updating the deversity enrolment block for $userId")
          }
      }
      case None => throw new MissingAccountException(s"No user account found for $userId")
    }
  }

  def findTeacher(teacherName: String, schoolName: String): Future[UserAccount] = {
    val query = BSONDocument("userName" -> teacherName, "deversityDetails.schoolName" -> schoolName)
    collection.find(query).one[UserAccount] map {
      case Some(acc)  => acc
      case None       => throw new MissingAccountException(s"No user account found matching teacher name $teacherName and school name $schoolName")
    }
  }

  def getAllTeacherForOrg(orgName: String): Future[List[UserAccount]] = {
    collection.find(deversitySchoolSelector(orgName)).cursor[UserAccount]().collect[List]()
  }
}
