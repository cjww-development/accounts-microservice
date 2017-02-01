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
package services

import config.{MongoFailedRead, MongoSuccessRead}
import models.{BasicDetails, Enrolments, Settings, UserAccount}
import repositories.RetrievalRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object GetDetailsService extends GetDetailsService {
  val retrievalRepository = RetrievalRepository
}

trait GetDetailsService {

  val retrievalRepository : RetrievalRepository

  def getBasicDetails(userId : String) : Future[Option[BasicDetails]] = {
    retrievalRepository.getAccount(userId) map {
      case MongoSuccessRead(account) => Some(extractBasicDetails(account.asInstanceOf[UserAccount]))
      case MongoFailedRead => None
    }
  }

  def getEnrolments(userId : String) : Future[Option[Enrolments]] = {
    retrievalRepository.getAccount(userId) map {
      case MongoSuccessRead(account) => extractEnrolments(account.asInstanceOf[UserAccount])
      case MongoFailedRead => None
    }
  }

  def getSettings(userId : String) : Future[Option[Settings]] = {
    retrievalRepository.getAccount(userId) map {
      case MongoSuccessRead(account) => extractSettings(account.asInstanceOf[UserAccount])
      case MongoFailedRead => None
    }
  }

  private def extractBasicDetails(user : UserAccount) : BasicDetails = {
    BasicDetails(
      firstName = user.firstName,
      lastName = user.lastName,
      userName = user.userName,
      email = user.email,
      metadata = user.metadata
    )
  }

  private def extractEnrolments(user : UserAccount) : Option[Enrolments] = {
    user.enrolments
  }

  private def extractSettings(user : UserAccount) : Option[Settings] = {
    user.settings.isDefined match {
      case false => None
      case true =>
        Some(
          Settings(
            user.settings.get.get("displayName"),
            user.settings.get.get("displayNameColour"),
            user.settings.get.get("displayImageURL")
          )
        )
    }
  }
}
