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

import reactivemongo.bson.BSONDocument

package object repositories {
  def getSelectorHead(selector: BSONDocument): (String, String) = (selector.elements.head.name, selector.elements.head.value.toString)

  val orgUserNameSelector: String => BSONDocument = orgUserName => BSONDocument("orgUserName" -> orgUserName)
  val orgEmailSelector: String => BSONDocument = orgEmail => BSONDocument("orgEmail" -> orgEmail)
  val orgIdSelector: String => BSONDocument = orgId => BSONDocument("orgId" -> orgId)

  val userIdSelector: String => BSONDocument = userId => BSONDocument("userId" -> userId)
  val userNameSelector: String => BSONDocument = userName => BSONDocument("userName" -> userName)
  val userEmailSelector: String => BSONDocument = email => BSONDocument("email" -> email)
  val userIdPasswordSelector: (String, String) => BSONDocument = (userId, password) => BSONDocument("userId" -> userId, "password" -> password)
  val deversitySchoolSelector: String => BSONDocument = orgName => BSONDocument(
    "deversityDetails.schoolName" -> orgName,
    "deversityDetails.role"       -> "teacher"
  )
}
