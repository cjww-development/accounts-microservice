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

package common

import javax.inject.Inject

import com.cjwwdev.auth.actions.{Authorisation, BaseAuth}
import com.cjwwdev.filters.RequestLoggingFilter
import com.cjwwdev.identifiers.IdentifierValidation
import com.cjwwdev.request.RequestParsers
import com.kenshoo.play.metrics.MetricsFilter
import play.api.http.DefaultHttpFilters
import play.api.mvc.Controller

trait BackendController
  extends Controller
    with RequestParsers
    with BaseAuth
    with Authorisation
    with IdentifierValidation

class EnabledFilters @Inject()(loggingFilter: RequestLoggingFilter, metricsFilter: MetricsFilter)
  extends DefaultHttpFilters(loggingFilter, metricsFilter)

class MissingAccountException(msg: String) extends Exception
class FailedToUpdateException(msg: String) extends Exception
class FailedToCreateException(msg: String) extends Exception

class OrganisationNotFoundException(msg: String) extends Exception
class FeedListEmptyException(msg: String) extends Exception

sealed trait UserNameUse
case object UserNameInUse extends UserNameUse
case object UserNameNotInUse extends UserNameUse

sealed trait EmailUse
case object EmailInUse extends EmailUse
case object EmailNotInUse extends EmailUse

sealed trait UpdatedPasswordResponse
case object InvalidOldPassword extends UpdatedPasswordResponse
case object PasswordUpdated extends UpdatedPasswordResponse
case object PasswordUpdateFailed extends UpdatedPasswordResponse

sealed trait UpdatedSettingsResponse
case object UpdatedSettingsSuccess extends UpdatedSettingsResponse
case object UpdatedSettingsFailed extends UpdatedSettingsResponse