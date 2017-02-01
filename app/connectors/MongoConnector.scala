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
package connectors

import config._
import play.api.Logger
import play.api.libs.json.OFormat
import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object MongoConnector extends MongoConnector {
  // $COVERAGE-OFF$
  val driver = new MongoDriver
  val mongoUri = MongoConnection.parseURI(s"$databaseUri").get
  val connection = driver.connection(mongoUri)
  val database = connection.database(mongoUri.db.get)

  def collection(name : String) : Future[JSONCollection] = {
    database map {
      _.collection(name)
    }
  }
  // $COVERAGE-ON$
}

trait MongoConnector extends MongoConfiguration {

  def create[T](collectionName : String, data : T)(implicit format : OFormat[T]) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.insert[T](data) map { res =>
        // $COVERAGE-OFF$
        if(res.hasErrors) Logger.error(s"[MongoConnector] - [create] : Inserting document of type ${data.getClass} FAILED reason : ${res.errmsg.get}")
        // $COVERAGE-ON$
        res.ok match {
          case false  => MongoFailedCreate
          case true   => MongoSuccessCreate
        }
      }
    }
  }

  // $COVERAGE-OFF$
  def read[T](collectionName : String, query : BSONDocument)(implicit format : OFormat[T]) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.find[BSONDocument](query).one[T] map { res =>
        if(res.isEmpty) Logger.info(s"[MongoConnector] - [read] : Query returned no results")
        res match {
          case Some(data) => MongoSuccessRead(data)
          case None       => MongoFailedRead
        }
      }
    }
  }
  // $COVERAGE-ON$

  // $COVERAGE-OFF$
  def readBulk[T](collectionName : String, query : BSONDocument)(implicit format : OFormat[T]) : Future[Option[List[T]]] = {
    collection(collectionName).flatMap {
      _.find(query).cursor[T].collect[List](). map {
        res => Some(res)
      }
    }
  }
  // $COVERAGE-ON$

  def update(collectionName : String, selectedData : BSONDocument, data : BSONDocument) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.update(selectedData, data) map { res =>
        // $COVERAGE-OFF$
        if(res.hasErrors) Logger.error(s"[MongoConnector] - [update] : Updating a document in $collectionName FAILED reason : ${res.errmsg.get}")
        // $COVERAGE-ON$
        res.ok match {
          case true   => MongoSuccessUpdate
          case false  => MongoFailedUpdate
        }
      }
    }
  }

  def delete[T](collectionName : String, query : BSONDocument) : Future[MongoResponse] = {
    collection(collectionName) flatMap {
      _.remove(query) map { res =>
        // $COVERAGE-OFF$
        if(res.hasErrors) Logger.error(s"[MongoConnector] - [delete] : Deleting a document from $collectionName FAILED reason : ${res.errmsg.get}")
        // $COVERAGE-ON$
        res.ok match {
          case true   => MongoSuccessDelete
          case false  => MongoFailedDelete
        }
      }
    }
  }
}
