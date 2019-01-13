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

package helpers.repositories

import com.cjwwdev.mongo.responses._
import common._
import helpers.other.Fixtures
import models.OrgAccount
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import repositories.OrgAccountRepository

import scala.concurrent.Future

trait MockOrgAccountRepository extends BeforeAndAfterEach with MockitoSugar with Fixtures {
  self: PlaySpec =>

  val mockOrgAccountRepo = mock[OrgAccountRepository]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockOrgAccountRepo)
  }

  def mockInsertNewOrgUser(inserted: Boolean): OngoingStubbing[Future[MongoCreateResponse]] = {
    when(mockOrgAccountRepo.insertNewOrgUser(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(inserted) Future.successful(MongoSuccessCreate) else Future.failed(new FailedToCreateException("")))
  }

  def mockGetOrgAccount(fetched: Boolean): OngoingStubbing[Future[OrgAccount]] = {
    when(mockOrgAccountRepo.getOrgAccount[OrgAccount](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(if(fetched) Future.successful(testOrgAccount) else Future.failed(new MissingAccountException("")))
  }

  def mockVerifyOrgUserName(inUse: Boolean): OngoingStubbing[Future[UserNameUse]] = {
    when(mockOrgAccountRepo.verifyUserName(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(inUse) UserNameInUse else UserNameNotInUse))
  }

  def mockVerifyOrgEmail(inUse: Boolean): OngoingStubbing[Future[EmailUse]] = {
    when(mockOrgAccountRepo.verifyEmail(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(inUse) EmailInUse else EmailNotInUse))
  }

  def mockDeleteOrgAccount(deleted: Boolean): OngoingStubbing[Future[MongoDeleteResponse]] = {
    when(mockOrgAccountRepo.deleteOrgAccount(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(deleted) MongoSuccessDelete else MongoFailedDelete))
  }
}
