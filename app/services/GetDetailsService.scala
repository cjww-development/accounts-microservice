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

import javax.inject.Inject
import models.{BasicDetails, Enrolments, Settings, UserAccount}
import reactivemongo.bson.BSONDocument
import repositories.UserAccountRepository

import scala.concurrent.{ExecutionContext => ExC, Future}

class DefaultGetDetailsService @Inject()(val userAccountRepository: UserAccountRepository) extends GetDetailsService

trait GetDetailsService {
  val userAccountRepository: UserAccountRepository

  private val userIdSelector: String => BSONDocument = userId => BSONDocument("userId" -> userId)

  def getBasicDetails(userId: String)(implicit ec: ExC): Future[BasicDetails] = {
    userAccountRepository.getUserBySelector(userIdSelector(userId)) map extractBasicDetails
  }

  def getEnrolments(userId: String)(implicit ec: ExC): Future[Option[Enrolments]] = {
    userAccountRepository.getUserBySelector(userIdSelector(userId)) map extractEnrolments
  }

  def getSettings(userId: String)(implicit ec: ExC): Future[Option[Settings]] = {
    userAccountRepository.getUserBySelector(userIdSelector(userId)) map extractSettings
  }

  private def extractBasicDetails(user: UserAccount): BasicDetails = BasicDetails(
    firstName = user.firstName,
    lastName  = user.lastName,
    userName  = user.userName,
    email     = user.email,
    createdAt = user.createdAt
  )

  private def extractEnrolments(user: UserAccount): Option[Enrolments] = user.enrolments
  private def extractSettings(user: UserAccount): Option[Settings]     = user.settings
}
