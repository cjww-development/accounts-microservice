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

import java.util.UUID

import com.cjwwdev.json.JsonFormats
import org.joda.time.DateTime
import play.api.data.validation.ValidationError
import play.api.libs.functional.syntax._
import play.api.libs.json._
import services.IdService

import scala.util.{Failure, Success, Try}

case class SourceDetail(service : String, location : String)

object SourceDetail extends JsonFormats[SourceDetail] {
  private val frontendServices  = List("auth-service", "deversity-frontend", "diagnostics-frontend")
  private val serviceValidation = Reads.StringReads.filter(ValidationError("Invalid service"))(frontendServices.contains(_))

  implicit val standardFormat: OFormat[SourceDetail] = (
    (__ \ "service").format[String](serviceValidation) and
    (__ \ "location").format[String]
  )(SourceDetail.apply, unlift(SourceDetail.unapply))
}

case class EventDetail(title : String, description : String)

object EventDetail extends JsonFormats[EventDetail] {
  implicit val standardFormat: OFormat[EventDetail] = (
    (__ \ "title").format[String] and
    (__ \ "description").format[String]
  )(EventDetail.apply, unlift(EventDetail.unapply))
}

case class FeedItem(feedId : String,
                    userId : String,
                    sourceDetail: SourceDetail,
                    eventDetail: EventDetail,
                    generated : DateTime)

object FeedItem extends JsonFormats[FeedItem] with IdService {

  private val userIdValidation = Reads.StringReads.filter(ValidationError("Invalid user id"))(userId =>
    if(userId.contains("user")) {
      Try(UUID.fromString(userId.replace(s"user-", ""))) match {
        case Success(_) => true
        case Failure(_) => false
      }
    } else {
      false
    }
  )

  def newFeedItemReads: Reads[FeedItem] = (
    (__ \ "feedId").read(generateFeedId) and
    (__ \ "userId").read[String](userIdValidation) and
    (__ \ "sourceDetail").read[SourceDetail] and
    (__ \ "eventDetail").read[EventDetail] and
    (__ \ "generated").read(DateTime.now())
  )(FeedItem.apply _)

  implicit val standardFormat: OFormat[FeedItem] = (
    (__ \ "feedId").format[String] and
    (__ \ "userId").format[String] and
    (__ \ "sourceDetail").format[SourceDetail](SourceDetail.standardFormat) and
    (__ \ "eventDetail").format[EventDetail](EventDetail.standardFormat) and
    (__ \ "generated").format[DateTime](dateTimeRead)(dateTimeWrite)
  )(FeedItem.apply, unlift(FeedItem.unapply))
}
