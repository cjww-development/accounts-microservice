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
package repositories

import com.cjwwdev.mongo.{MongoConnector, MongoSuccessRead}
import helpers.CJWWSpec
import models.UserAccount
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

class RetrievalRepositorySpec extends CJWWSpec {

  val testAccount =
    UserAccount(
      Some("testId"),
      "testFirst",
      "testLast",
      "testUser",
      "test@email.com",
      "testPassword",
      None,
      None,
      None
    )

  class Setup {
    val mockMongoConnector = mock[MongoConnector]
    val testRepo = new RetrievalRepository(mockMongoConnector)
  }

  "getAccount" should {
    "return a MongoSuccessRead" when {
      "given a userId" in new Setup {
        when(mockMongoConnector.read[UserAccount](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.successful(MongoSuccessRead(testAccount)))

        val result = await(testRepo.getAccount("testId"))
      }
    }
  }
}
