package com.charlieworld.benchmark.dbms.datastore

import com.charlieworld.benchmark.dbms.{DataPoint, Repository}
import com.spotify.asyncdatastoreclient._
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jay on 01/09/2017.
  */
class DatastoreRepository()(implicit ec: ExecutionContext, datastore: Datastore)
  extends Repository
    with GetAllEntities
    with EntityAndDataPoint
    with DatastoreKey {

  def datetime(millis: Long): String =
    new DateTime(millis, DateTimeZone.UTC).toString

  def upsertBatch(dps: Seq[DataPoint]): Batch = {
    val batch = QueryBuilder.batch
    for {
      dp <- dps
      ent = dp.toEntity
      stm = QueryBuilder.update(ent).upsert()
    } yield {
      batch.add(stm)
    }
    batch
  }

  override def putDataPoints(dps: Seq[DataPoint]): Future[Unit] = {
    val numBatches = (dps.size / 500) + 1
    val opFutures = for {
      dps500 <- dps.grouped(numBatches)
      batch = upsertBatch(dps500)
    } yield {
      datastore.executeAsync(batch).asScala.map(_ => ())
    }
    Future.sequence(opFutures).map(_ => ())
  }

  override def getDataPoints(key: String, start: Long, end: Long, desc: Boolean): Future[Seq[DataPoint]] = {
    val order = if (desc) QueryBuilder.desc("__key__") else QueryBuilder.asc("__key__")
    val query = QueryBuilder
      .query()
      .kindOf(datastoreKind)
      .filterBy(QueryBuilder.gte("__key__", datastoreKey(datastoreKind, key, start)))
      .filterBy(QueryBuilder.lte("__key__", datastoreKey(datastoreKind, key, end)))
      .orderBy(order)
    for {
      entities <- getAllEntities(query)
    } yield entities.map(_.toDataPoint)
  }
}