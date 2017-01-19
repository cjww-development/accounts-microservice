// Copyright (C) 2011-2012 the original author or authors.
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
package utils.security

import helpers.CJWWSpec

class EncryptionSpec extends CJWWSpec {

  class Setup {
    object TestSec extends Encryption
  }

  "sha512" should {
    "return a string exactly 128 chars long and isnt equal to what was input" in new Setup {
      val result = TestSec.sha512("testString")
      result.length mustBe 128
      assert(result != "testString")
    }

    "encrypt a string that is five chars long and still return a 128 char string" in new Setup {
      val result = TestSec.sha512("aaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is ten chars long and still return a 128 char string" in new Setup {
      val result = TestSec.sha512("aaaaaaaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is fifty chars long and still return a 128 char string" in new Setup {
      val result = TestSec.sha512("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is one hundred chars long and still return a 128 char string" in new Setup {
      val result = TestSec.sha512("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
      result.length mustBe 128
    }

    "encrypt a string that is two hundred chars long and still return a 128 char string" in new Setup {
      val result = TestSec.sha512("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
      result.length mustBe 128
    }
  }
}
