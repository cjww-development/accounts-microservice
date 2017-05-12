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

import com.cjwwdev.reactivemongo.MongoCreateResponse
import com.google.inject.{Inject, Singleton}
import models.{OrgAccount, UserAccount}
import repositories.{OrgAccountRepo, OrgAccountRepository, UserAccountRepo, UserAccountRepository}

import scala.concurrent.Future

@Singleton
class RegistrationService @Inject()(userAccountRepository: UserAccountRepository, orgAccountRepository: OrgAccountRepository) {

  val userAccountStore: UserAccountRepo = userAccountRepository.store
  val orgAccountstore: OrgAccountRepo = orgAccountRepository.store

  def createNewUser(newUser : UserAccount) : Future[MongoCreateResponse] = {
    userAccountStore.insertNewUser(UserAccount.newUser(newUser))
  }

  def createNewOrgUser(newOrgUser: OrgAccount): Future[MongoCreateResponse] = {
    orgAccountstore.insertNewOrgUser(OrgAccount.newOrgUser(newOrgUser))
  }
}
