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

import com.cjwwdev.mongo.indexes.RepositoryIndexer
import javax.inject.Inject
import repositories.{OrgAccountRepository, UserAccountRepository, UserFeedRepository}

import scala.concurrent.ExecutionContext

class AccountsIndexing @Inject()(val orgAccountRepository: OrgAccountRepository,
                                 val userAccountRepository: UserAccountRepository,
                                 val userFeedRepository: UserFeedRepository,
                                 implicit val ec: ExecutionContext) extends RepositoryIndexer {
  override val repositories = Seq(
    orgAccountRepository,
    userAccountRepository,
    userFeedRepository
  )
  runIndexing
}
