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
package config

import com.typesafe.config.ConfigFactory
import utils.security.Encryption

trait ConfigurationStrings {
  val config = ConfigFactory.load

  val env = config.getString("cjww.environment")

  val AUTH_MICROSERVICE_ID = Encryption.sha512(config.getString(s"$env.application-ids.auth-microservice"))
  val API_ID = Encryption.sha512(config.getString(s"$env.application-ids.rest-api"))
  val AUTH_ID = Encryption.sha512(config.getString(s"$env.application-ids.auth-service"))
  val DIAG_ID = Encryption.sha512(config.getString(s"$env.application-ids.diagnostics-frontend"))
  val DEV_ID = Encryption.sha512(config.getString(s"$env.application-ids.deversity-frontend"))

  val databaseUri = config.getString(s"$env.mongo.uri")
}
