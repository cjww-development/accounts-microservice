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

import com.cjwwdev.regex.RegexPack
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Settings(displayName : String,
                    displayNameColour : String,
                    displayImageURL : String)

object Settings extends RegexPack {
  private val displayNameValidation = {
    val options = List("full", "short", "user")
    Reads.StringReads.filter(JsonValidationError("Invalid display name option"))(options.contains(_))
  }

  private val displayNameColourValidation = Reads.StringReads.filter(JsonValidationError("Invalid hex colour"))(_.matches(hexadecimalColourRegex.regex))
  private val displayImageUrlValidation   = Reads.StringReads.filter(JsonValidationError("Invalid url"))(url => url.matches(urlRegex.regex) || url.equals(defaultUrl))

  implicit val standardFormat: OFormat[Settings] = (
    (__ \ "displayName").format[String](displayNameValidation) and
    (__ \ "displayNameColour").format[String](displayNameColourValidation) and
    (__ \ "displayImageURL").format[String]
  )(Settings.apply, unlift(Settings.unapply))
}
