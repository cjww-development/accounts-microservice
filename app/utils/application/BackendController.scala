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

package utils.application

import com.cjwwdev.logging.Logger
import com.cjwwdev.security.encryption.DataSecurity
import play.api.libs.json.{Format, Reads}
import play.api.mvc.{Controller, Request, Result}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

trait BackendController extends Controller {
  protected def decryptRequest[T](f : T => Future[Result])(implicit request : Request[String], manifest: Manifest[T], reads : Reads[T], format : Format[T]) = {
    Try(DataSecurity.decryptInto[T](request.body.toString)) match {
      case Success(Some(data)) =>
        Logger.info("[BackendController] - [decryptRequest] : Request decryption successful")
        f(data)
      case Success(None) => Future.successful(BadRequest)
      case Failure(e) =>
        Logger.error(s"[BackendController] - [decryptRequest] : Request body decryption has FAILED BODY: ${request.body.toString}")
        Future.successful(BadRequest)
    }
  }

  protected def decryptUrl[T](enc: String)(f : T => Future[Result])(implicit format : Format[T]) = {
    Try(DataSecurity.decryptInto[T](enc)) match {
      case Success(Some(data)) =>
        Logger.info("[BackendController] - [decryptRequest] : Request decryption successful")
        f(data)
      case Success(None) => Future.successful(BadRequest)
      case Failure(e) =>
        Logger.error(s"[BackendController] - [decryptRequest] : Request body decryption has FAILED")
        Future.successful(BadRequest)
    }
  }
}
