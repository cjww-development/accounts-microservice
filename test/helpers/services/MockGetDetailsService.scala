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
import models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import services.GetDetailsService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MockGetDetailsService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockGetDetailsService = mock[GetDetailsService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockGetDetailsService)
  }

  def mockGetBasicDetails: OngoingStubbing[Future[BasicDetails]] = {
    when(mockGetDetailsService.getBasicDetails(ArgumentMatchers.any()))
      .thenReturn(Future(testBasicDetails))
  }

  def mockGetEnrolments(fetched: Boolean): OngoingStubbing[Future[Option[Enrolments]]] = {
    when(mockGetDetailsService.getEnrolments(ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testEnrolments) else None))
  }

  def mockGetSettings(fetched: Boolean): OngoingStubbing[Future[Option[Settings]]] = {
    when(mockGetDetailsService.getSettings(ArgumentMatchers.any()))
      .thenReturn(Future(if(fetched) Some(testSettings) else None))
  }
}
