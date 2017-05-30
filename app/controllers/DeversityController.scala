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

package controllers

import javax.inject.{Inject, Singleton}

import com.cjwwdev.auth.actions.{Authorisation, Authorised, NotAuthorised}
import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.reactivemongo.{MongoFailedUpdate, MongoSuccessUpdate}
import com.cjwwdev.request.RequestParsers
import com.cjwwdev.security.encryption.DataSecurity
import models.{DeversityEnrolment, OrgDetails, TeacherDetails}
import play.api.mvc.{Action, AnyContent, Controller}
import services.DeversityService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DeversityController @Inject()(deversityService: DeversityService, authConnect: AuthConnector) extends Controller with RequestParsers with Authorisation {

  val authConnector: AuthConnector = authConnect

  def getDeversityInformation(userId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(userId) {
        case Authorised => deversityService.getDeversityUserInformation(userId) map {
          case Some(deversityDetails)   => Ok(DataSecurity.encryptType[DeversityEnrolment](deversityDetails).get)
          case None                     => NotFound
        }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def updateDeversityInformation(userId: String): Action[String] = Action.async(parse.text) {
    implicit request =>
      authorised(userId) {
        case Authorised =>
          decryptRequest[DeversityEnrolment](DeversityEnrolment.standardFormat) { details =>
            deversityService.updateDeversityUserInformation(userId, details) map {
              case MongoSuccessUpdate => Ok
              case MongoFailedUpdate  => InternalServerError
            }
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def updatedDeversityId(userId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised(userId) {
        case Authorised => deversityService.createOrUpdateEnrolments(userId) map {
          devId => Ok(devId)
        } recover {
          case _ => InternalServerError
        }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def findSchool(orgName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          decryptUrl(orgName) { name =>
            deversityService.findSchool(name) map (found => if(found) Ok else NotFound)
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def getSchoolDetails(orgName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          decryptUrl(orgName) { name =>
            deversityService.getSchoolDetails(name) map {
              case Some(details) => Ok(DataSecurity.encryptType[OrgDetails](details).get)
              case None => NotFound
            }
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def findTeacher(userName: String, schoolName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          decryptUrl(userName) { uName =>
            decryptUrl(schoolName) { sName =>
              deversityService.findTeacher(uName, sName) map(found => if(found) Ok else NotFound)
            }
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }

  def getTeacherDetails(orgName: String, schoolName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        case Authorised =>
          decryptUrl(orgName) { oName =>
            decryptUrl(schoolName) { sName =>
              deversityService.getTeacherDetails(oName, sName) map {
                case Some(details) => Ok(DataSecurity.encryptType[TeacherDetails](details).get)
                case None => NotFound
              }
            }
          }
        case NotAuthorised => Future.successful(Forbidden)
      }
  }
}
