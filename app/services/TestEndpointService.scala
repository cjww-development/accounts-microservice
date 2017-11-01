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

package services

import javax.inject.{Inject, Singleton}

import com.cjwwdev.reactivemongo.MongoDeleteResponse
import models.OrgAccount
import repositories.{OrgAccountRepository, UserAccountRepository, UserFeedRepository}
import selectors.UserAccountSelectors._
import selectors.OrgAccountSelectors._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TestEndpointService @Inject()(userAccountRepository: UserAccountRepository,
                                    orgAccountRepository: OrgAccountRepository,
                                    userFeedRepository: UserFeedRepository) {

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
