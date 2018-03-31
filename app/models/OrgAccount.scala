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

case class OrgAccount(orgId: String,
                      deversityId: String,
                      orgName: String,
                      initials: String,
                      orgUserName: String,
                      location: String,
                      orgEmail: String,
                      credentialType: String,
                      password: String,
                      createdAt: DateTime,
                      settings: Option[Settings])

object OrgAccount extends IdService with RegexPack with TimeFormat {
  private val orgNameValidation     = Reads.StringReads.filter(ValidationError("Invalid org name"))(_.matches(orgNameRegex.regex))
  private val initialsValidation    = Reads.StringReads.filter(ValidationError("Invalid initials"))(_.matches(initialsRegex.regex))
  private val orgUserNameValidation = Reads.StringReads.filter(ValidationError("Invalid org user name"))(_.matches(userNameRegex.regex))
  private val locationValidation    = Reads.StringReads.filter(ValidationError("Invalid location"))(_.matches(locationRegex.regex))
  private val emailValidation       = Reads.StringReads.filter(ValidationError("Invalid email address"))(_.matches(emailRegex.regex))
  private val passwordValidation    = Reads.StringReads.filter(ValidationError("Invalid password"))(_.length == 128)

  def newOrgAccountReads: Reads[OrgAccount] = (
    (__ \ "orgId").read(generateOrgId) and
    (__ \ "deversityId").read(generateDevId) and
    (__ \ "orgName").read[String](orgNameValidation) and
    (__ \ "initials").read[String](initialsValidation) and
    (__ \ "orgUserName").read[String](orgUserNameValidation) and
    (__ \ "location").read[String](locationValidation) and
    (__ \ "orgEmail").read[String](emailValidation) and
    (__ \ "credentialType").read("organisation") and
    (__ \ "password").read[String](passwordValidation) and
    (__ \ "createdAt").read(DateTime.now) and
    (__ \ "settings").read(None)
  )(OrgAccount.apply _)

  implicit val standardFormat: OFormat[OrgAccount] = (
    (__ \ "orgId").format[String] and
    (__ \ "deversityId").format[String] and
    (__ \ "orgName").format[String] and
    (__ \ "initials").format[String] and
    (__ \ "orgUserName").format[String] and
    (__ \ "location").format[String] and
    (__ \ "orgEmail").format[String] and
    (__ \ "credentialType").format[String] and
    (__ \ "password").format[String] and
    (__ \ "createdAt").format[DateTime](dateTimeRead)(dateTimeWrite) and
    (__ \ "settings").formatNullable[Settings]
  )(OrgAccount.apply, unlift(OrgAccount.unapply))
}

case class OrgDetails(orgName: String, initials: String, location: String)

object OrgDetails {
  implicit val standardFormat: OFormat[OrgDetails] = (
    (__ \ "orgName").format[String] and
    (__ \ "initials").format[String] and
    (__ \ "location").format[String]
  )(OrgDetails.apply, unlift(OrgDetails.unapply))
}
