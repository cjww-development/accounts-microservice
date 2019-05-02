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

package connectors

import java.util.UUID

import com.rabbitmq.client.AMQP.BasicProperties
import com.rabbitmq.client.Channel
import helpers.other.AssertionHelpers
import helpers.rabbit.MockChannel
import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsValue, Json}

class AuditingConnectorSpec extends PlaySpec with MockChannel with AssertionHelpers {

  val testConnector = new AuditingConnector {
    override val userName: String     = "testUser"
    override val password: String     = "testPass"
    override val host: String         = "localhost"
    override val queueName: String    = "testQueue"
    override val channel: Channel     = mockChannel
  }

  val createProps: BasicProperties = new BasicProperties(
    "text/json",
    "plain",
    null,
    2,
    null,
    s"correlationId-${UUID.randomUUID()}",
    null,
    null,
    s"message-${UUID.randomUUID()}",
    null,
    null,
    null,
    null,
    null
  )

  def messageBody(msgType: String): JsValue = Json.parse(
    s"""
      |{
      |   "messageType" : "$msgType",
      |   "correlationId" : "${createProps.getCorrelationId}",
      |   "eventCode" : 1
      |}
    """.stripMargin
  )

  "publishMessage" should {
    "return an AuditingSuccess" when {
      "a message has been successfully published to the queue (AUDIT_EVENT)" in {
        mockBasicPublish(success = true)

        assertReturn(testConnector.publishMessage(createProps, messageBody("AUDIT_EVENT"))) {
          _ mustBe AuditingSuccess
        }
      }

      "a message has been successfully published to the queue (FEED_EVENT)" in {
        mockBasicPublish(success = true)

        assertReturn(testConnector.publishMessage(createProps, messageBody("FEED_EVENT"))) {
          _ mustBe AuditingSuccess
        }
      }
    }

    "return an AuditingFailure" when {
      "there was a problem publishing the message to the queue (AUDIT_EVENT)" in {
        mockBasicPublish(success = false)

        assertReturn(testConnector.publishMessage(createProps, messageBody("AUDIT_EVENT"))) {
          _ mustBe AuditingFailed
        }
      }

      "there was a problem publishing the message to the queue (FEED_EVENT)" in {
        mockBasicPublish(success = false)

        assertReturn(testConnector.publishMessage(createProps, messageBody("FEED_EVENT"))) {
          _ mustBe AuditingFailed
        }
      }
    }
  }
}
