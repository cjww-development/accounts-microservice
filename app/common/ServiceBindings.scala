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

package common

import com.cjwwdev.config.{ConfigurationLoader, DefaultConfigurationLoader}
import com.cjwwdev.featuremanagement.models.Features
import com.cjwwdev.health.{DefaultHealthController, HealthController}
import com.cjwwdev.logging.filters.{DefaultRequestLoggingFilter, RequestLoggingFilter}
import com.cjwwdev.mongo.indexes.RepositoryIndexer
import controllers._
import play.api.{Configuration, Environment}
import play.api.inject.{Binding, Module}
import repositories._
import services._

class ServiceBindings extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] =
    bindOther() ++ bindRepositories() ++ bindServices() ++ bindControllers()

  private def bindControllers(): Seq[Binding[_]] = Seq(
    bind(classOf[OrgAccountController]).to(classOf[DefaultOrgAccountController]).eagerly(),
    bind(classOf[RegistrationController]).to(classOf[DefaultRegistrationController]).eagerly(),
    bind(classOf[TestTeardownController]).to(classOf[DefaultTestTeardownController]).eagerly(),
    bind(classOf[UpdateUserDetailsController]).to(classOf[DefaultUpdateUserDetailsController]).eagerly(),
    bind(classOf[UserDetailsController]).to(classOf[DefaultUserDetailsController]).eagerly(),
    bind(classOf[UserFeedController]).to(classOf[DefaultUserFeedController]).eagerly(),
    bind(classOf[ValidationController]).to(classOf[DefaultValidationController]).eagerly(),
    bind(classOf[HealthController]).to(classOf[DefaultHealthController]).eagerly()
  )

  private def bindServices(): Seq[Binding[_]] = Seq(
    bind(classOf[AccountService]).to(classOf[DefaultAccountService]).eagerly(),
    bind(classOf[GetDetailsService]).to(classOf[DefaultGetDetailsService]).eagerly(),
    bind(classOf[OrgAccountService]).to(classOf[DefaultOrgAccountService]).eagerly(),
    bind(classOf[RegistrationService]).to(classOf[DefaultRegistrationService]).eagerly(),
    bind(classOf[TestEndpointService]).to(classOf[DefaultTestEndpointService]).eagerly(),
    bind(classOf[UserFeedService]).to(classOf[DefaultUserFeedService]).eagerly(),
    bind(classOf[ValidationService]).to(classOf[DefaultValidationService]).eagerly()
  )

  private def bindRepositories(): Seq[Binding[_]] = Seq(
    bind(classOf[OrgAccountRepository]).to(classOf[DefaultOrgAccountRepository]).eagerly(),
    bind(classOf[UserAccountRepository]).to(classOf[DefaultUserAccountRepository]).eagerly(),
    bind(classOf[UserFeedRepository]).to(classOf[DefaultUserFeedRepository]).eagerly(),
    bind(classOf[RepositoryIndexer]).to(classOf[AccountsIndexing]).eagerly()
  )

  private def bindOther(): Seq[Binding[_]] = Seq(
    bind(classOf[ConfigurationLoader]).to(classOf[DefaultConfigurationLoader]).eagerly(),
    bind(classOf[Features]).to(classOf[FeatureDef]).eagerly(),
    bind(classOf[RequestLoggingFilter]).to(classOf[DefaultRequestLoggingFilter]).eagerly()
  )
}
