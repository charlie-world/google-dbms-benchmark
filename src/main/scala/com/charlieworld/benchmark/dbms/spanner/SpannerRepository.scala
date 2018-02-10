package com.charlieworld.benchmark.dbms.spanner

import java.util.concurrent.TimeUnit

import com.charlieworld.benchmark.dbms.{DataPoint, Repository}
import com.google.cloud.spanner.{DatabaseClient, Mutation, Statement, TimestampBound}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future, blocking}

/**
  * @author Charlie Lee 
  * @date 2017. 11. 23.
  */
class SpannerRepository()(implicit ec: ExecutionContext, spannerClient: DatabaseClient) extends Repository {

  override def getDataPoints(key: String, start: Long, end: Long, desc: Boolean): Future[Vector[DataPoint]] = {
    val sql =
      s"""
         |select key, value, timestamp
         |from $spannerTable
         |where id >='${keyMaker(key, start)}' and id <= '${keyMaker(key, end)}'
         |order by id desc
      """.stripMargin
    Future {
      blocking {
        val results = spannerClient.singleUse(TimestampBound.ofMaxStaleness(1, TimeUnit.SECONDS)).executeQuery(Statement.of(sql))
        try {
          getAllEntity(Vector(), results)
        } finally {
          results.close()
        }
      }
    }
  }

  override def putDataPoints(dps: Seq[DataPoint]): Future[Unit] = {
    val mutations: Seq[Mutation] = dps
    Future {
      spannerClient.write(mutations.asJava)
    }
  }
}
