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

package services

import com.cjwwdev.mongo.responses.MongoDeleteResponse
import javax.inject.Inject
import models.OrgAccount
import reactivemongo.bson.BSONDocument
import repositories.{OrgAccountRepository, UserAccountRepository, UserFeedRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TestEndpointServiceImpl @Inject()(val userAccountRepository: UserAccountRepository,
                                        val orgAccountRepository: OrgAccountRepository,
                                        val userFeedRepository: UserFeedRepository) extends TestEndpointService

trait TestEndpointService {
  val userAccountRepository: UserAccountRepository
  val orgAccountRepository: OrgAccountRepository
  val userFeedRepository: UserFeedRepository

  private val userNameSelector: String => BSONDocument = userName => BSONDocument("userName" -> userName)
  private val orgUserNameSelector: String => BSONDocument = orgUserName => BSONDocument("orgUserName" -> orgUserName)

  def tearDownTestUser(userName: String): Future[MongoDeleteResponse] = {
    for {
      account <- userAccountRepository.getUserBySelector(userNameSelector(userName))
      _       <- userFeedRepository.deleteFeedItems(account.userId)
      deleted <- userAccountRepository.deleteUserAccount(account.userId)
    } yield deleted
  }

  def tearDownTestOrgUser(orgUserName: String): Future[MongoDeleteResponse] = {
    for {
      account <- orgAccountRepository.getOrgAccount[OrgAccount](orgUserNameSelector(orgUserName))
      deleted <- orgAccountRepository.deleteOrgAccount(account.orgId)
    } yield deleted
  }
}
