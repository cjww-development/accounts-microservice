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
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import services.IdService

case class SourceDetail(service : String, location : String)

object SourceDetail extends JsonFormats[SourceDetail] {
  implicit val standardFormat: OFormat[SourceDetail] = (
    (__ \ "service").format[String] and
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

  val newFeedItemReads: Reads[FeedItem] = new Reads[FeedItem] {
    override def reads(json: JsValue): JsResult[FeedItem] = {
      JsSuccess(FeedItem(
        feedId = generateFeedId,
        userId = json.\("userId").as[String],
        sourceDetail = SourceDetail(
          service = json.\("sourceDetail").\("service").as[String],
          location = json.\("sourceDetail").\("location").as[String]
        ),
        eventDetail = EventDetail(
          title = json.\("eventDetail").\("title").as[String],
          description = json.\("eventDetail").\("description").as[String]
        ),
        generated = DateTime.now
      ))
    }
  }

  implicit val standardFormat: OFormat[FeedItem] = (
    (__ \ "feedId").format[String] and
    (__ \ "userId").format[String] and
    (__ \ "sourceDetail").format[SourceDetail](SourceDetail.standardFormat) and
    (__ \ "eventDetail").format[EventDetail](EventDetail.standardFormat) and
    (__ \ "generated").format[DateTime](dateTimeRead)(dateTimeWrite)
  )(FeedItem.apply, unlift(FeedItem.unapply))
}
