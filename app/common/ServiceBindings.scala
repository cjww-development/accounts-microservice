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

import com.google.inject.AbstractModule
import repositories._
import services._
import controllers._

class ServiceBindings extends AbstractModule {
  override def configure(): Unit = {
    bindRepositories()
    bindServices()
    bindControllers()
  }

  private def bindControllers(): Unit = {
    bind(classOf[OrgAccountController]).to(classOf[OrgAccountControllerImpl]).asEagerSingleton()
    bind(classOf[RegistrationController]).to(classOf[RegistrationControllerImpl]).asEagerSingleton()
    bind(classOf[TestTeardownController]).to(classOf[TestTeardownControllerImpl]).asEagerSingleton()
    bind(classOf[UpdateUserDetailsController]).to(classOf[UpdateUserDetailsControllerImpl]).asEagerSingleton()
    bind(classOf[UserDetailsController]).to(classOf[UserDetailsControllerImpl]).asEagerSingleton()
    bind(classOf[UserFeedController]).to(classOf[UserFeedControllerImpl]).asEagerSingleton()
    bind(classOf[ValidationController]).to(classOf[ValidationControllerImpl]).asEagerSingleton()
  }

  private def bindServices(): Unit = {
    bind(classOf[AccountService]).to(classOf[AccountServiceImpl]).asEagerSingleton()
    bind(classOf[GetDetailsService]).to(classOf[GetDetailsServiceImpl]).asEagerSingleton()
    bind(classOf[OrgAccountService]).to(classOf[OrgAccountServiceImpl]).asEagerSingleton()
    bind(classOf[RegistrationService]).to(classOf[RegistrationServiceImpl]).asEagerSingleton()
    bind(classOf[TestEndpointService]).to(classOf[TestEndpointServiceImpl]).asEagerSingleton()
    bind(classOf[UserFeedService]).to(classOf[UserFeedServiceImpl]).asEagerSingleton()
    bind(classOf[ValidationService]).to(classOf[ValidationServiceImpl]).asEagerSingleton()
  }

  private def bindRepositories(): Unit = {
    bind(classOf[OrgAccountRepository]).to(classOf[OrgAccountRepositoryImpl]).asEagerSingleton()
    bind(classOf[UserAccountRepository]).to(classOf[UserAccountRepositoryImpl]).asEagerSingleton()
    bind(classOf[UserFeedRepository]).to(classOf[UserFeedRepositoryImpl]).asEagerSingleton()
  }
}
