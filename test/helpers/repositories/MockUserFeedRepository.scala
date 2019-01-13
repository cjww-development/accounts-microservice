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

package helpers.repositories

import com.cjwwdev.mongo.responses._
import helpers.other.Fixtures
import models.FeedItem
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import repositories.UserFeedRepository

import scala.concurrent.Future

trait MockUserFeedRepository extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockUserFeedRepo = mock[UserFeedRepository]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserFeedRepo)
  }

  def mockCreateFeedItem(created: Boolean): OngoingStubbing[Future[MongoCreateResponse]] = {
    when(mockUserFeedRepo.createFeedItem(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(created) MongoSuccessCreate else MongoFailedCreate))
  }

  def mockGetFeedItems(found: Boolean): OngoingStubbing[Future[List[FeedItem]]] = {
    when(mockUserFeedRepo.getFeedItems(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(found) List(testFeedItem) else List.empty[FeedItem]))
  }

  def mockDeleteFeedItems(deleted: Boolean): OngoingStubbing[Future[MongoDeleteResponse]] = {
    when(mockUserFeedRepo.deleteFeedItems(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(deleted) MongoSuccessDelete else MongoFailedDelete))
  }
}
