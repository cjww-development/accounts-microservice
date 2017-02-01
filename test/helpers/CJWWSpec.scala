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

package helpers

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import config.{ConfigurationStrings, MongoCollections}
import mocks.MongoMocks
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.ws.ahc.AhcWSClient

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

trait CJWWSpec extends PlaySpec with OneAppPerSuite with MongoMocks with MockitoSugar with ConfigurationStrings with MongoCollections  {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  def await[T](future : Awaitable[T]) : T = {
    Await.result(future, 5.seconds)
  }
}