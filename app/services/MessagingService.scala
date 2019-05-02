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

package services

import java.time.LocalDateTime
import java.util.UUID

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.http.headers.HeaderPackage
import com.cjwwdev.implicits.ImplicitDataSecurity._
import com.rabbitmq.client.AMQP
import com.rabbitmq.client.AMQP.BasicProperties
import connectors.{AuditingConnector, AuditingResponse}
import javax.inject.Inject
import models.auditing.{Event => AuditEvent}
import models.common.MessageTypes._
import play.api.libs.json.{Json, Writes}
import play.api.mvc.Request

class DefaultMessagingService @Inject()(val auditingConnector: AuditingConnector,
                                        val config: ConfigurationLoader) extends MessagingService {
  override val appId: String    = config.getServiceId(config.get[String]("appName"))
  override val appName: String  = config.get[String]("appName")
  override val enabled: Boolean = config.get[Boolean]("messaging.auditing.enabled")
}

trait MessagingService {

  val auditingConnector: AuditingConnector

  val appId: String
  val appName: String

  val enabled: Boolean

  def sendAuditEvent[T](userId: String, eventCode: Int, detail: T)(implicit writes: Writes[T], req: Request[_]): AuditingResponse = {
    val event = buildAuditEvent(userId, eventCode, detail)
    val props = createProperties(2, event.correlationId)
    auditingConnector.publishMessage(props, Json.toJson(event))
  }

  private def buildAuditEvent[T](userId: String, eventCode: Int, detail: T)(implicit req: Request[_], writes: Writes[T]): AuditEvent = AuditEvent(
    correlationId = s"correlationId-${UUID.randomUUID.toString}",
    messageType   = AUDIT_EVENT,
    service       = appName,
    appId         = appId,
    createdAt     = LocalDateTime.now,
    sessionId     = getHeaderPackage.flatMap(_.cookieId).getOrElse("-"),
    userId        = userId,
    requestId     = req.headers.get("requestId").getOrElse("-"),
    deviceId      = "NOT-IMPLEMENTED",
    ipAddress     = "0.0.0.0",
    eventCode     = eventCode,
    detail        = Json.toJson(detail)
  )

  private def createProperties(priority: Int, cId: String): AMQP.BasicProperties = {
    new BasicProperties(
      "text/json",
      "plain",
      null,
      2,
      null,
      cId,
      null,
      null,
      s"message-${UUID.randomUUID()}",
      null,
      null,
      null,
      null,
      null
    )
  }

  private def getHeaderPackage(implicit request: Request[_]): Option[HeaderPackage] = {
    request.headers.get("cjww-headers") map {
      _.decrypt[HeaderPackage].fold(
        identity,
        _ => HeaderPackage.build(appId)
      )
    }
  }
}
