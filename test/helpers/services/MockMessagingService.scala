/*
 * Copyright 2019 CJWW Development
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

import connectors.{AuditingFailed, AuditingResponse, AuditingSuccess}
import helpers.other.Fixtures
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import services.MessagingService

trait MockMessagingService extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockMessagingService: MessagingService = mock[MessagingService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockMessagingService)
  }

  def mockSendAuditEvent(success: Boolean): OngoingStubbing[AuditingResponse] = {
    when(mockMessagingService.sendAuditEvent(any(), any(), any())(any(), any()))
      .thenReturn(if(success) AuditingSuccess else AuditingFailed)
  }
}
