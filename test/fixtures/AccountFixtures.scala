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
package fixtures

import models.{DeversityEnrolment, OrgAccount, UserAccount}
import org.joda.time.{DateTime, DateTimeZone}

object AccountFixtures {

  final val now = new DateTime(DateTimeZone.UTC)

  val testOrgAccount = OrgAccount(
    orgId           = "org-test-org-id",
    deversityId     = "org-test-dev-id",
    orgName         = "testOrgName",
    initials        = "TI",
    orgUserName     = "testOrgUserName",
    location        = "testLocation",
    orgEmail        = "test@email.com",
    credentialType  = "organisation",
    password        = "testPass",
    createdAt       = now,
    settings        = None
  )

  val testAccount = UserAccount(
    userId            = "testUserId",
    firstName         = "testFirstName",
    lastName          = "testLastName",
    userName          = "testUserName",
    email             = "test@email.com",
    password          = "testPass",
    deversityDetails  = Some(DeversityEnrolment(
      statusConfirmed = "pending",
      schoolName      = "testOrgName",
      role            = "teacher",
      title           = Some("Prof"),
      room            = Some("testRoom"),
      teacher         = None
    )),
    createdAt         = now,
    enrolments        = None,
    settings          = None
  )
}
