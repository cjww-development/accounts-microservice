/*
* Copyright 2017 HM Revenue & Customs
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

import config.{MongoCollections, MongoResponse}
import connectors.MongoConnector
import models.UserAccount
import reactivemongo.bson.BSONDocument

import scala.concurrent.Future

object RetrievalRepository extends RetrievalRepository {
  val mongoConnector = MongoConnector
}

trait RetrievalRepository extends MongoCollections {
  val mongoConnector : MongoConnector

  def getAccount(userId : String) : Future[MongoResponse] = {
    mongoConnector.read[UserAccount](USER_ACCOUNTS, BSONDocument("_id" -> userId))
  }
}
