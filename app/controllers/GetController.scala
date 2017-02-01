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
package controllers

import models.{BasicDetails, Enrolments, Settings}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import services.GetDetailsService
import utils.security.DataSecurity

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GetController extends GetCtrl {
  val detailsService = GetDetailsService
}

trait GetCtrl extends Controller {

  val detailsService : GetDetailsService

  def getBasicDetails(id : String) : Action[AnyContent] = Action.async {
    implicit request =>
      Logger.debug(s"ID: $id")
      detailsService.getBasicDetails(id) map {
        case Some(details) =>
          DataSecurity.encryptData[BasicDetails](details) match {
            case Some(enc) => Ok(enc)
            case None => InternalServerError
          }
        case None => NotFound
      }
  }

  def getEnrolments(id : String) : Action[AnyContent] = Action.async {
    implicit request =>
      Logger.debug(s"ID: $id")
      detailsService.getEnrolments(id) map {
        case Some(enrolments) =>
          DataSecurity.encryptData[Enrolments](enrolments) match {
            case Some(enc) => Ok(enc)
            case None => InternalServerError
          }
        case None => NotFound
      }
  }

  def getSettings(id : String) : Action[AnyContent] = Action.async {
    implicit request =>
      Logger.debug(s"ID: $id")
      detailsService.getSettings(id) map {
        case Some(settings) =>
          DataSecurity.encryptData[Settings](settings) match {
            case Some(enc) =>
              Logger.debug("OK")
              Ok(enc)
            case None =>
              Logger.debug("ISE")
              InternalServerError
          }
        case None => NotFound
      }
  }
}
