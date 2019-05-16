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
import helpers.other.{AccountEnums, Fixtures}
import models.UserAccount
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import repositories.UserAccountRepository

import scala.concurrent.Future

trait MockUserAccountRepository extends BeforeAndAfterEach with MockitoSugar with Fixtures{
  self: PlaySpec =>

  val mockUserAccountRepo = mock[UserAccountRepository]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAccountRepo)
  }

  def mockInsertNewUser(inserted: Boolean): OngoingStubbing[Future[MongoCreateResponse]] = {
    when(mockUserAccountRepo.insertNewUser(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(inserted) Future.successful(MongoSuccessCreate) else Future.failed(new FailedToCreateException("")))
  }

  def mockGetUserBySelector(returned: UserAccount): OngoingStubbing[Future[UserAccount]] = {
    when(mockUserAccountRepo.getUserBySelector(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(returned))
  }

  def mockGetUserBySelectorFailed(): OngoingStubbing[Future[UserAccount]] = {
    when(mockUserAccountRepo.getUserBySelector(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.failed(new MissingAccountException("")))
  }

  def mockVerifyUserName(inUse: Boolean): OngoingStubbing[Future[UserNameUse]] = {
    when(mockUserAccountRepo.verifyUserName(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(inUse) UserNameInUse else UserNameNotInUse))
  }

  def mockVerifyEmail(inUse: Boolean): OngoingStubbing[Future[EmailUse]] = {
    when(mockUserAccountRepo.verifyEmail(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(inUse) EmailInUse else EmailNotInUse))
  }

  def mockUpdateAccountData(updated: Boolean): OngoingStubbing[Future[MongoUpdatedResponse]] = {
    when(mockUserAccountRepo.updateAccountData(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(updated) Future.successful(MongoSuccessUpdate) else Future.failed(new FailedToUpdateException("")))
  }

  def mockFindPassword(found: Boolean): OngoingStubbing[Future[Boolean]] = {
    when(mockUserAccountRepo.findPassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(found) Future.successful(true) else Future.failed(new MissingAccountException("")))
  }

  def mockUpdatePassword(updated: Boolean): OngoingStubbing[Future[MongoUpdatedResponse]] = {
    when(mockUserAccountRepo.updatePassword(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(updated) Future.successful(MongoSuccessUpdate) else Future.failed(new FailedToUpdateException("")))
  }

  def mockUpdateSettings(updated: Boolean): OngoingStubbing[Future[MongoUpdatedResponse]] = {
    when(mockUserAccountRepo.updateSettings(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(if(updated) Future.successful(MongoSuccessUpdate) else Future.failed(new FailedToUpdateException("")))
  }

  def mockGetAllTeacherForOrg(found: Boolean): OngoingStubbing[Future[List[UserAccount]]] = {
    when(mockUserAccountRepo.getAllTeacherForOrg(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(found) List(testUserAccount(AccountEnums.teacher)) else List.empty[UserAccount]))
  }

  def mockDeleteUserAccount(deleted: Boolean): OngoingStubbing[Future[MongoDeleteResponse]] = {
    when(mockUserAccountRepo.deleteUserAccount(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(if(deleted) MongoSuccessDelete else MongoFailedDelete))
  }
}
