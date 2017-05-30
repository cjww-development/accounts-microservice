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

package mocks

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.cjwwdev.auth.connectors.AuthConnector
import com.cjwwdev.auth.models.{AuthContext, User}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.mvc.{Action, AnyContent, Result}
import play.api.test.FakeRequest
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

object AuthBuilder extends SessionBuild {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  final val now = new DateTime(DateTimeZone.UTC)

  implicit val testContext =
    AuthContext(
      "context-1234567890",
      User(
        "user-766543",
        Some("testFirstName"),
        Some("testLastName"),
        None,
        "individual",
        None
      ),
      "testLink",
      "testLink",
      "testLink",
      now
    )

  def buildAuthorisedUserAndGet(action: Action[AnyContent],
                                mockAuthConnector: AuthConnector)(test: Future[Result] => Any): Any = {

    val request = buildRequestWithSession

    when(mockAuthConnector.getContext(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(testContext)))

    val result = action.apply(request).run()
    test(result)
  }

  def buildAuthorisedUserAndPost[T](action: Action[T],
                             mockAuthConnector: AuthConnector,
                             request: FakeRequest[T])(test: Future[Result] => Any): Any = {
    when(mockAuthConnector.getContext(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(testContext)))

    val result = action.apply(request)
    test(result)
  }
}