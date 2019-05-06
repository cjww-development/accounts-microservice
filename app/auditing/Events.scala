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

package auditing

object Events {
  final case class EventType(code: Int, description: String)

  val individualReg = EventType(code = 100, "Individual Registration Event")
  val orgReg        = EventType(code = 101, "Organisation Registration Event")

  val indDetailsUpdate  = EventType(code = 200, "Individual Basic Details Update Event")
  val indPasswordUpdate = EventType(code = 202, "Individual Password Update Event")
  val indSettingsUpdate = EventType(code = 204, "Individual Settings Update Event")
}
