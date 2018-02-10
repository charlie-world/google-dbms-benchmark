package com.charlieworld.benchmark.dbms.datastore

import com.charlieworld.benchmark.dbms.tools.AsScalaFuture
import com.spotify.asyncdatastoreclient.{Datastore, Entity, Query}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jay on 20/11/2017.
  */
trait GetAllEntities extends AsScalaFuture {

  def getAllEntities(query: Query)(implicit ec: ExecutionContext, datastore: Datastore): Future[Seq[Entity]] =
    for {
      queryResult <- datastore.executeAsync(query).asScala
      entities = queryResult.getAll.asScala
      result <- if (entities.isEmpty)
        Future.successful(Nil)
      else
        getAllEntities(query.fromCursor(queryResult.getCursor))
    } yield entities ++ result
}
