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
import common.{FailedToCreateException, MissingAccountException, _}
import javax.inject.Inject
import models._
import play.api.Configuration
import play.api.libs.json.OFormat
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class OrgAccountRepositoryImpl @Inject()(val config: Configuration) extends OrgAccountRepository with ConnectionSettings

trait OrgAccountRepository extends DatabaseRepository with Logging {
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

  def insertNewOrgUser(orgUser: OrgAccount): Future[MongoCreateResponse] = {
    for {
      col <- collection
      wr  <- col.insert[OrgAccount](orgUser)
    } yield if(wr.ok) MongoSuccessCreate else throw new FailedToCreateException("Failed to create new OrgAccount")
  }

  def getOrgAccount[T : OFormat](selector: BSONDocument): Future[T] = {
    for {
      col           <- collection
      (name, value) =  getSelectorHead(selector)
      acc           <- col.find(selector).one[T]
    } yield acc.getOrElse({
      logger.error(s"[getOrgAccount] - Could not find user account based on $name with value $value")
      throw new MissingAccountException(s"No user account found based on $name with value $value")
    })
  }

  def verifyUserName(username : String): Future[UserNameUse] = {
    for {
      col <- collection
      acc <- col.find(orgUserNameSelector(username)).one[OrgAccount]
    } yield if(acc.isDefined) {
      logger.info(s"[verifyUserName] : This user name is already in use on this system")
      UserNameInUse
    } else {
      UserNameNotInUse
    }
  }

  def verifyEmail(email : String): Future[EmailUse] = {
    for {
      col <- collection
      acc <- col.find(orgEmailSelector(email)).one[OrgAccount]
    } yield if(acc.isDefined) {
      logger.info(s"[verifyUserName] : This user name is already in use on this system")
      EmailInUse
    } else {
      EmailNotInUse
    }
  }

  def deleteOrgAccount(userId: String): Future[MongoDeleteResponse] = {
    for {
      col <- collection
      wr  <- col.remove(orgIdSelector(userId))
    } yield if(wr.ok) MongoSuccessDelete else MongoFailedDelete
  }
}