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

import com.cjwwdev.reactivemongo.MongoUpdatedResponse
import com.cjwwdev.security.encryption.DataSecurity
import models.{DeversityEnrolment, OrgDetails, TeacherDetails}
import repositories.{OrgAccountRepository, UserAccountRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DeversityService @Inject()(userAccountRepository: UserAccountRepository, orgAccountRepository: OrgAccountRepository) {

  def findSchool(orgUserName: String): Future[Boolean] = {
    orgAccountRepository.findSchool(orgUserName) map {
      acc => true
    } recover {
      case _: Throwable => false
    }
  }

  def findTeacher(userName: String, schoolName: String): Future[Boolean] = {
    userAccountRepository.findTeacher(userName, schoolName) map {
      acc => true
    } recover {
      case _: Throwable => false
    }
  }

  def getTeacherDetails(userName: String, schoolName: String): Future[Option[TeacherDetails]] = {
    userAccountRepository.findTeacher(userName, schoolName) map {
      acc => acc.deversityDetails match {
        case Some(details)  => Some(TeacherDetails(details.title.get, acc.lastName, details.room.get, details.statusConfirmed))
        case None           => None
      }
    } recover {
      case _: Throwable     => None
    }
  }

  def getSchoolDetails(orgUserName: String): Future[Option[OrgDetails]] = {
    orgAccountRepository.getSchoolDetails(orgUserName) map {
      details => Some(details)
    } recover {
      case _: Throwable => None
    }
  }

  def getDeversityUserInformation(userId: String): Future[Option[DeversityEnrolment]] = {
    userAccountRepository.getAccount(userId) map {
      acc => acc.deversityDetails
    }
  }

  def updateDeversityUserInformation(userId: String, deversityDetails: DeversityEnrolment): Future[MongoUpdatedResponse] = {
    userAccountRepository.updateDeversityDataBlock(userId, deversityDetails)
  }

  def createOrUpdateEnrolments(userId: String): Future[String] = {
    userAccountRepository.updateDeversityEnrolment(userId) map(devId => DataSecurity.encryptString(devId))
  }
}
