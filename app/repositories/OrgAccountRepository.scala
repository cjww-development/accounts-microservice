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

import com.cjwwdev.logging.Logger
import com.cjwwdev.reactivemongo.{MongoConnector, MongoCreateResponse, MongoRepository, MongoSuccessCreate}
import config.Exceptions.{FailedToCreateException, MissingAccountException}
import config._
import models._
import reactivemongo.api.DB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class OrgAccountRepository @Inject()() extends MongoConnector {
  val store = new OrgAccountRepo(db)
}

class OrgAccountRepo(db: () => DB) extends MongoRepository("org-accounts", db) {

  override def indexes: Seq[Index] = Seq(
    Index(
      key = Seq("orgId" -> IndexType.Ascending),
      name = Some("OrgId"),
      unique = true,
      sparse = false
    )
  )

  private def orgUserNameSelector(orgUserName: String): BSONDocument = BSONDocument("orgUserName" -> orgUserName)
  private def orgIdSelector(orgId: String): BSONDocument = BSONDocument("orgId" -> orgId)

  def insertNewOrgUser(orgUser: OrgAccount): Future[MongoCreateResponse] = {
    collection.insert(orgUser) map { writeResult =>
      if(writeResult.ok) MongoSuccessCreate else throw new FailedToCreateException("Failed to create new OrgAccount")
    }
  }

  def verifyUserName(username : String) : Future[UserNameUse] = {
    collection.find(orgUserNameSelector(username)).one[OrgAccount] map {
      case Some(_)  =>
        Logger.info(s"[OrgAccountRepository] - [verifyUserName] : This user name is already in use on this system")
        UserNameInUse
      case None     => UserNameNotInUse
    }
  }

  def verifyEmail(email : String) : Future[EmailUse] = {
    collection.find(BSONDocument("email" -> email)).one[OrgAccount] map {
      case Some(_)  =>
        Logger.info(s"[OrgAccountRepository] - [verifyEmail] : This email address is already in use on this system")
        EmailInUse
      case None     => EmailNotInUse
    }
  }

  def findSchool(orgName: String): Future[OrgAccount] = {
    collection.find(orgUserNameSelector(orgName)).one[OrgAccount] map {
      case Some(acc)  => acc
      case None       => throw new MissingAccountException(s"No org account found for org name $orgName")
    }
  }

  def getSchoolDetails(orgName: String): Future[OrgDetails] = {
    collection.find(orgUserNameSelector(orgName)).one[OrgDetails] map {
      case Some(details)  => details
      case None           => throw new MissingAccountException(s"No org account for org name $orgName")
    }
  }

  def getOrgDetails(orgId: String): Future[OrgDetails] = {
    collection.find(orgIdSelector(orgId)).one[OrgDetails] map {
      case Some(details)  => details
      case None           => throw new MissingAccountException(s"No org account for org id $orgId")
    }
  }

  def getOrgAccount(orgId: String): Future[OrgAccount] = {
    collection.find(orgIdSelector(orgId)).one[OrgAccount] map {
      case Some(acc) => acc
      case None => throw new MissingAccountException(s"No org account for org id $orgId")
    }
  }
}
