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

package models

import com.cjwwdev.json.JsonFormats
import com.cjwwdev.regex.RegexPack
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class UserProfile(firstName : String,
                       lastName : String,
                       userName : String,
                       email : String,
                       settings : Option[Settings])

object UserProfile extends JsonFormats[UserProfile] with RegexPack {
  private val firstNameValidation = Reads.StringReads.filter(ValidationError("Invalid first name"))(_.matches(firstNameRegex.regex))
  private val lastNameValidation = Reads.StringReads.filter(ValidationError("Invalid last name"))(_.matches(lastNameRegex.regex))
  private val userNameValidation = Reads.StringReads.filter(ValidationError("Invalid user name"))(_.matches(userNameRegex.regex))
  private val emailValidation = Reads.StringReads.filter(ValidationError("Invalid email address"))(_.matches(emailRegex.regex))


  implicit val standardFormat: OFormat[UserProfile] = (
    (__ \ "firstName").format[String](firstNameValidation) and
    (__ \ "lastName").format[String](lastNameValidation) and
    (__ \ "userName").format[String](userNameValidation) and
    (__ \ "email").format[String](emailValidation) and
    (__ \ "settings").formatNullable[Settings]
  )(UserProfile.apply, unlift(UserProfile.unapply))
}
