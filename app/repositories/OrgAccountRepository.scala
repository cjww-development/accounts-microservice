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

import javax.inject.Inject

import com.cjwwdev.reactivemongo._
import common.{FailedToCreateException, MissingAccountException, _}
import models._
import play.api.Logger
import play.api.libs.json.OFormat
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import selectors.OrgAccountSelectors._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrgAccountRepositoryImpl @Inject extends OrgAccountRepository

trait OrgAccountRepository extends MongoDatabase {
  override def indexes: Seq[Index] = Seq(
    Index(
      key     = Seq("orgId" -> IndexType.Ascending),
      name    = Some("OrgId"),
      unique  = true,
      sparse  = false
    ),
    Index(
      key     = Seq("deversityId" -> IndexType.Ascending),
      name    = Some("DeversityId"),
      unique  = true,
      sparse  = false
    )
  )

  private def getSelectorHead(selector: BSONDocument): (String, String) = (selector.elements.head._1, selector.elements.head._2.toString)

  def insertNewOrgUser(orgUser: OrgAccount): Future[MongoCreateResponse] = {
    collection flatMap {
      _.insert(orgUser) map { wr =>
        if(wr.ok) MongoSuccessCreate else throw new FailedToCreateException("Failed to create new OrgAccount")
      }
    }
  }

  def getOrgAccount[T : OFormat](selector: BSONDocument): Future[T] = collection flatMap {
    val elements = getSelectorHead(selector)
    _.find(selector).one[T] map {
      case Some(acc) => acc
      case _         =>
        Logger.error(s"[UserAccountRepository] - [getUserBySelector] - Could not find user account based on ${elements._1} with value ${elements._2}")
        throw new MissingAccountException(s"No user account found based on ${elements._1} with value ${elements._2}")
    }
  }

  def verifyUserName(username : String) : Future[UserNameUse] = {
    collection flatMap {
      _.find(orgUserNameSelector(username)).one[OrgAccount] map {
        case Some(_) =>
          Logger.info(s"[OrgAccountRepository] - [verifyUserName] : This user name is already in use on this system")
          UserNameInUse
        case None =>
          UserNameNotInUse
      }
    }
  }

  def verifyEmail(email : String) : Future[EmailUse] = {
    collection flatMap {
      _.find(BSONDocument("orgEmail" -> email)).one[OrgAccount] map {
        case Some(_) =>
          Logger.info(s"[OrgAccountRepository] - [verifyEmail] : This email address is already in use on this system")
          EmailInUse
        case None =>
          EmailNotInUse
      }
    }
  }

  def deleteOrgAccount(userId: String): Future[MongoDeleteResponse] = {
    collection flatMap {
      _.remove(orgIdSelector(userId)) map { wr =>
        if(wr.ok) MongoSuccessDelete else MongoFailedDelete
      }
    }
  }
}