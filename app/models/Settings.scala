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
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Settings(displayName : Option[String],
                    displayNameColour : Option[String],
                    displayImageURL : Option[String])

object Settings extends JsonFormats[Settings] {
  implicit val standardFormat: OFormat[Settings] = (
    (__ \ "displayName").formatNullable[String] and
    (__ \ "displayNameColour").formatNullable[String] and
    (__ \ "displayImageURL").formatNullable[String]
  )(Settings.apply, unlift(Settings.unapply))
}
