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

import com.cjwwdev.config.ConfigurationLoader
import com.cjwwdev.implicits.ImplicitJsValues._
import com.cjwwdev.logging.Logging
import com.cjwwdev.security.deobfuscation.DeObfuscation.stringDeObfuscate
import com.cjwwdev.security.deobfuscation.DeObfuscator
import com.rabbitmq.client.{AMQP, Channel, ConnectionFactory}
import javax.inject.Inject
import play.api.libs.json.JsValue

import scala.util.Try

sealed trait AuditingResponse
case object AuditingSuccess  extends AuditingResponse
case object AuditingFailed   extends AuditingResponse
case object AuditingDisabled extends AuditingResponse

class DefaultAuditingConnector @Inject()(val config: ConfigurationLoader) extends AuditingConnector {
  private val configPath: String = "messaging.rabbitMQ"

  private def getEncodedValue(key: String)(implicit deObfuscator: DeObfuscator[String]): String = {
    deObfuscator.decrypt(config.get[String](s"$configPath.$key")).swap.getOrElse("")
  }

  override val host: String      = config.get[String](s"$configPath.host")
  override val userName: String  = getEncodedValue("user")
  override val password: String  = getEncodedValue("pass")
  override val queueName: String = config.get[String](s"$configPath.queue")

  protected val connectionFactory = new ConnectionFactory()

  connectionFactory.setHost(host)
  connectionFactory.setUsername(userName)
  connectionFactory.setPassword(password)

  override val channel: Channel = connectionFactory
    .newConnection()
    .createChannel()
}

trait AuditingConnector extends Logging {

  val host: String

  val userName: String
  val password: String

  val queueName: String

  val channel: Channel

  def publishMessage(props: AMQP.BasicProperties, body: JsValue): AuditingResponse = {
    Try(channel.basicPublish("", queueName, props, body.toString.getBytes)).fold(
      _ => {
        logger.warn(s"[publishMessage] - ${createFailureLog(body)}")
        AuditingFailed
      },
      _ => {
        logger.info(s"[publishMessage] - ${createSuccessLog(body)}")
        AuditingSuccess
      }
    )
  }

  private def createFailureLog(json: JsValue): String = {
    val messageType   = json.get[String]("messageType")
    val correlationId = json.get[String]("correlationId")
    messageType match {
      case "AUDIT_EVENT" =>
        val eventCode = json.get[Int]("eventCode")
        s"There was a problem publishing the message with cId $correlationId of type $messageType with event code $eventCode to rabbitMQ $queueName"
      case "FEED_EVENT"  =>
        s"There was a problem publishing the message with cId $correlationId of type $messageType to rabbitMQ $queueName"
    }
  }

  private def createSuccessLog(json: JsValue): String = {
    s"Message of type ${json.get[String]("messageType")} published against cId ${json.get[String]("correlationId")}"
  }
}
