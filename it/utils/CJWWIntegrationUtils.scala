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
package utils

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.{WS, WSRequest}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import repositories.{OrgAccountRepository, UserAccountRepository}

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

trait CJWWIntegrationUtils extends PlaySpec with GuiceOneServerPerSuite {

  val userAccountRepository = new UserAccountRepository
  val orgAccountRepository = new OrgAccountRepository

  val baseUrl = s"http://localhost:$port/accounts"

  def client(url: String): WSRequest = WS.url(url)

  def await[T](awaitable: Awaitable[T]): T = Await.result(awaitable, 5.seconds)

  def afterITest(): Unit = {
    userAccountRepository.store.collection.remove(BSONDocument("userName" -> "testUserName"))
    orgAccountRepository.store.collection.remove(BSONDocument("orgUserName" -> "testOrgUserName"))
  }
}