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

package helpers.services

import helpers.other.Fixtures
import models.FeedItem
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsObject, Json}
import services.UserFeedService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MockUserFeedService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockUserFeedService = mock[UserFeedService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserFeedService)
  }

  def mockCreateFeedItem(created: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockUserFeedService.createFeedItem(ArgumentMatchers.any()))
      .thenReturn(Future(created))
  }

  def mockFlipList(list: List[FeedItem]): OngoingStubbing[Option[List[FeedItem]]] = {
    when(mockUserFeedService.flipList(ArgumentMatchers.any()))
      .thenReturn(if(list.isEmpty) None else Some(list.reverse))
  }

  def mockGetFeedList(fetched: Boolean): OngoingStubbing[Future[Option[JsObject]]] = {
    when(mockUserFeedService.getFeedList(ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(Json.obj("feed-array" -> Json.toJson(testFeedList))) else None))
  }
}
