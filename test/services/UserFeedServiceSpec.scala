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
package services

import helpers.services.ServiceSpec

class UserFeedServiceSpec extends ServiceSpec {

  class Setup {
    val testService = new UserFeedService {
      override val userFeedRepository = mockUserFeedRepo
    }
  }

  "createFeedItem" should {
    "return true" when {
      "the feed item has been successfully created" in new Setup {
        mockCreateFeedItem(created = true)

        awaitAndAssert(testService.createFeedItem(testFeedItem)) { res =>
          assert(res)
        }
      }
    }

    "return false" when {
      "there was a problem creating the feed item" in new Setup {
        mockCreateFeedItem(created = false)

        awaitAndAssert(testService.createFeedItem(testFeedItem)) { res =>
          assert(!res)
        }
      }
    }
  }

  "flipList" should {
    "return a reversed list" when {
      "given an optional list" in new Setup {
        assertReturn(testService.flipList(testFeedList)) {
          _ mustBe Some(testFeedList.reverse)
        }
      }
    }

    "return none" when {
      "the given list is empty" in new Setup {
        assertReturn(testService.flipList(List())) {
          _ mustBe None
        }
      }
    }
  }

  "getFeedList" should {
    "return a JsObject with a feed-array JsArray in it" when {
      "given a userId" in new Setup {
        mockGetFeedItems(found = true)

        awaitAndAssert(testService.getFeedList(generateTestSystemId(USER))) {
          _.get.value.contains("feed-array") mustBe true
        }
      }
    }
  }
}
