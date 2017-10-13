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
package services

import org.scalatestplus.play.PlaySpec

class IdServiceSpec extends PlaySpec {

  class Setup {
    object TestService extends IdService
  }

  "generateUserId" should {
    "return a string that contains 'user' and is exactly 40 characters long" in new Setup {
      val resultOne = TestService.generateUserId
      val resultTwo = TestService.generateUserId
      val resultThree = TestService.generateUserId
      val resultFour = TestService.generateUserId
      val resultFive = TestService.generateUserId

      assert(resultOne.contains("user"))
      assert(resultTwo.contains("user"))
      assert(resultThree.contains("user"))
      assert(resultFour.contains("user"))
      assert(resultFive.contains("user"))

      resultOne.length mustBe 41
      resultTwo.length mustBe 41
      resultThree.length mustBe 41
      resultFour.length mustBe 41
      resultFive.length mustBe 41
    }
  }

  "generateOrgId" should {
    "return a string that contains 'org' and is exactly 41 characters long" in new Setup {
      val resultOne = TestService.generateOrgId
      val resultTwo = TestService.generateOrgId
      val resultThree = TestService.generateOrgId
      val resultFour = TestService.generateOrgId
      val resultFive = TestService.generateOrgId

      assert(resultOne.contains("org-user"))
      assert(resultTwo.contains("org-user"))
      assert(resultThree.contains("org-user"))
      assert(resultFour.contains("org-user"))
      assert(resultFive.contains("org-user"))

      resultOne.length mustBe 45
      resultTwo.length mustBe 45
      resultThree.length mustBe 45
      resultFour.length mustBe 45
      resultFive.length mustBe 45
    }
  }

  "generateFeedId" should {
    "return a string that contains 'feed-item' and is exactly 46 characters long" in new Setup {
      val resultOne = TestService.generateFeedId
      val resultTwo = TestService.generateFeedId
      val resultThree = TestService.generateFeedId
      val resultFour = TestService.generateFeedId
      val resultFive = TestService.generateFeedId

      assert(resultOne.contains("feed-item"))
      assert(resultTwo.contains("feed-item"))
      assert(resultThree.contains("feed-item"))
      assert(resultFour.contains("feed-item"))
      assert(resultFive.contains("feed-item"))

      resultOne.length mustBe 46
      resultTwo.length mustBe 46
      resultThree.length mustBe 46
      resultFour.length mustBe 46
      resultFive.length mustBe 46
    }
  }
}
