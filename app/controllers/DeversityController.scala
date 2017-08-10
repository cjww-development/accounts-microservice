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

import com.cjwwdev.auth.actions.Authorisation
import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.identifiers.IdentifierValidation
import com.cjwwdev.reactivemongo.{MongoFailedUpdate, MongoSuccessUpdate}
import com.cjwwdev.request.RequestParsers
import com.cjwwdev.security.encryption.DataSecurity
import models.{DeversityEnrolment, OrgDetails, TeacherDetails}
import play.api.Logger
import play.api.mvc.{Action, AnyContent, Controller}
import services.DeversityService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class DeversityController @Inject()(deversityService: DeversityService,
                                    authConnect: AuthConnector) extends Controller with RequestParsers with Authorisation with IdentifierValidation {

  val authConnector: AuthConnector = authConnect

  def getDeversityInformation(userId: String): Action[AnyContent] = Action.async {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) {
          deversityService.getDeversityUserInformation(userId) map {
            case Some(deversityDetails)   => Ok(DataSecurity.encryptType[DeversityEnrolment](deversityDetails))
            case None                     => NotFound
          }
        }
      }
  }

  def updateDeversityInformation(userId: String): Action[String] = Action.async(parse.text) {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) {
          withJsonBody[DeversityEnrolment] { details =>
            deversityService.updateDeversityUserInformation(userId, details) map {
              case MongoSuccessUpdate => Ok
              case MongoFailedUpdate  => InternalServerError
            }
          }
        }
      }
  }

  def updatedDeversityId(userId: String): Action[AnyContent] = Action.async {
    implicit request =>
      validateAs(USER, userId) {
        authorised(userId) {
          deversityService.createOrUpdateEnrolments(userId) map {
            devId => Ok(devId)
          } recover {
            case _ => InternalServerError
          }
        }
      }
  }

  def findSchool(orgName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        decryptUrl(orgName) { name =>
          deversityService.findSchool(name) map (found => if(found) Ok else NotFound)
        }
      }
  }

  def getSchoolDetails(orgName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        decryptUrl(orgName) { name =>
          deversityService.getSchoolDetails(name) map {
            case Some(details) => Ok(DataSecurity.encryptType[OrgDetails](details))
            case None          => NotFound
          }
        }
      }
  }

  def findTeacher(userName: String, schoolName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        decryptUrl(userName) { uName =>
          decryptUrl(schoolName) { sName =>
            deversityService.findTeacher(uName, sName) map(found => if(found) Ok else NotFound)
          }
        }
      }
  }

  def getTeacherDetails(orgName: String, schoolName: String): Action[AnyContent] = Action.async {
    implicit request =>
      openActionVerification {
        decryptUrl(orgName) { oName =>
          decryptUrl(schoolName) { sName =>
            deversityService.getTeacherDetails(oName, sName) map {
              case Some(details) => Ok(DataSecurity.encryptType[TeacherDetails](details))
              case None          => NotFound
            }
          }
        }
      }
  }

  def getPendingEnrolmentsCount(orgId: String): Action[AnyContent] = Action.async {
    implicit request =>
      validateAs(ORG_USER, orgId) {
        authorised(orgId) {
          deversityService.getPendingDeversityEnrolmentCount(orgId) map { count =>
            Ok(DataSecurity.encryptType(count))
          } recover {
            case _ => InternalServerError
          }
        }
      }
  }

}