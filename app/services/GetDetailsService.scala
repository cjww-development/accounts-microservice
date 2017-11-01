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

import models.{BasicDetails, Enrolments, Settings, UserAccount}
import repositories.UserAccountRepository
import selectors.UserAccountSelectors._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class GetDetailsService @Inject()(userAccountRepository: UserAccountRepository) {

  def getBasicDetails(userId : String) : Future[BasicDetails] = {
    userAccountRepository.getUserBySelector(userIdSelector(userId)) map(acc => extractBasicDetails(acc))
  }

  def getEnrolments(userId : String) : Future[Option[Enrolments]] = {
    userAccountRepository.getUserBySelector(userIdSelector(userId)) map(acc => extractEnrolments(acc))
  }

  def getSettings(userId : String) : Future[Option[Settings]] = {
    userAccountRepository.getUserBySelector(userIdSelector(userId)) map(acc => extractSettings(acc))
  }

  private def extractBasicDetails(user : UserAccount) : BasicDetails = {
    BasicDetails(
      firstName = user.firstName,
      lastName = user.lastName,
      userName = user.userName,
      email = user.email,
      createdAt = user.createdAt
    )
  }

  private def extractEnrolments(user : UserAccount) : Option[Enrolments] = {
    user.enrolments
  }

  private def extractSettings(user : UserAccount) : Option[Settings] = {
    user.settings
  }
}
