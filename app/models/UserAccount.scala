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
package models

import com.cjwwdev.json.TimeFormat
import com.cjwwdev.regex.RegexPack
import org.joda.time.DateTime
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._
import services.IdService

case class DeversityEnrolment(schoolDevId: String,
                              role: String,
                              title: Option[String],
                              room: Option[String],
                              teacher: Option[String])

object DeversityEnrolment extends RegexPack {

  val roleRead = Reads.StringReads.filter(JsonValidationError("Invalid role"))(role => role.equals("teacher") || role.equals("student"))
  val roleWrite: Writes[String] = new OWrites[String] {
    override def writes(o: String) = Json.obj("role" -> o)
  }

  implicit val standardFormat: OFormat[DeversityEnrolment] = (
    (__ \ "schoolDevId").format[String] and
    (__ \ "role").format[String](roleRead) and
    (__ \ "title").formatNullable[String] and
    (__ \ "room").formatNullable[String] and
    (__ \ "teacher").formatNullable[String]
  )(DeversityEnrolment.apply, unlift(DeversityEnrolment.unapply))
}

case class Enrolments(hubId : Option[String],
                      diagId : Option[String],
                      deversityId : Option[String])

object Enrolments {
  implicit val standardFormat: OFormat[Enrolments] = (
    (__ \ "hubId").formatNullable[String] and
    (__ \ "diagId").formatNullable[String] and
    (__ \ "deversityId").formatNullable[String]
  )(Enrolments.apply, unlift(Enrolments.unapply))
}

case class UserAccount(userId : String,
                       firstName : String,
                       lastName : String,
                       userName : String,
                       email : String,
                       password : String,
                       deversityDetails: Option[DeversityEnrolment],
                       createdAt : DateTime,
                       enrolments: Option[Enrolments],
                       settings : Option[Settings])

object UserAccount extends IdService with RegexPack with TimeFormat {
  private val firstNameValidation = Reads.StringReads.filter(JsonValidationError("Invalid first name"))(_.matches(firstNameRegex.regex))
  private val lastNameValidation  = Reads.StringReads.filter(JsonValidationError("Invalid last name"))(_.matches(lastNameRegex.regex))
  private val userNameValidation  = Reads.StringReads.filter(JsonValidationError("Invalid user name"))(_.matches(userNameRegex.regex))
  private val emailValidation     = Reads.StringReads.filter(JsonValidationError("Invalid email address"))(_.matches(emailRegex.regex))
  private val passwordValidation  = Reads.StringReads.filter(JsonValidationError("Invalid password"))(_.length == 128)

  def newUserReads: Reads[UserAccount] = (
    (__ \ "userId").read[String](generateUserId) and
    (__ \ "firstName").read[String](firstNameValidation) and
    (__ \ "lastName").read[String](lastNameValidation) and
    (__ \ "userName").read[String](userNameValidation) and
    (__ \ "email").read[String](emailValidation) and
    (__ \ "password").read[String](passwordValidation) and
    (__ \ "deversityDetails").read(None) and
    (__ \ "createdAt").read(DateTime.now) and
    (__ \ "enrolments").read(None) and
    (__ \ "settings").read(None)
  )(UserAccount.apply _)

  implicit val standardFormat: OFormat[UserAccount] = (
    (__ \ "userId").format[String] and
    (__ \ "firstName").format[String] and
    (__ \ "lastName").format[String] and
    (__ \ "userName").format[String] and
    (__ \ "email").format[String] and
    (__ \ "password").format[String] and
    (__ \ "deversityDetails").formatNullable[DeversityEnrolment] and
    (__ \ "createdAt").format[DateTime](dateTimeRead)(dateTimeWrite) and
    (__ \ "enrolments").formatNullable[Enrolments] and
    (__ \ "settings").formatNullable[Settings]
  )(UserAccount.apply, unlift(UserAccount.unapply))
}

case class BasicDetails(firstName : String,
                        lastName : String,
                        userName : String,
                        email : String,
                        createdAt : DateTime)

object BasicDetails extends TimeFormat {
  implicit val standardFormat: OFormat[BasicDetails] = (
    (__ \ "firstName").format[String] and
    (__ \ "lastName").format[String] and
    (__ \ "userName").format[String] and
    (__ \ "email").format[String] and
    (__ \ "createdAt").format[DateTime](dateTimeRead)(dateTimeWrite)
  )(BasicDetails.apply, unlift(BasicDetails.unapply))
}
