package com.charlieworld.benchmark.dbms.datastore

import com.charlieworld.benchmark.dbms.DataPoint
import com.charlieworld.benchmark.dbms.tools.{AsScalaFuture, Await}
import com.spotify.asyncdatastoreclient.{Entity, QueryBuilder}
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future

/**
  * Created by Charlie 09/09/2017
  * */
class DatastoreRepositorySpec
  extends FlatSpec
    with DatastoreTestKit
    with DatastoreKey
    with Await
    with AsScalaFuture
    with Matchers {

  implicit val repository: DatastoreRepository = new DatastoreRepository()(executionContext, datastore)
  private val dpKey = "KEY"

  private def putDataPoints(dps: Seq[DataPoint]): Unit = {
    dps foreach { dp =>
      val v = dp.value
      val k = datastoreKey(datastoreKind, dp.key, dp.timestamp)
      val entity = Entity.builder().key(k).property("value", v, false).build()
      val insert = QueryBuilder.insert(entity)
      datastore.executeAsync(insert).asScala.await()
    }
  }

  private def test[T](dps: Seq[DataPoint], expected: T, actual: => Future[T]): Unit = {
    putDataPoints(dps)
    actual.await() shouldBe expected
  }

  "getDataPoints" should "get time-series data points from datastore" ignore {
    val timestamp = new DateTime(0, DateTimeZone.UTC)
    val value = 0.0
    val fixtures = Seq(DataPoint(dpKey, value, timestamp.getMillis))
    val expected = Seq(DataPoint(dpKey, value, timestamp.getMillis))
    lazy val actual = repository.getDataPoints(
      dpKey,
      timestamp.getMillis,
      timestamp.plusSeconds(1).getMillis,
      desc = true
    )

    test(fixtures, expected, actual)
  }

  it should "get more than 300 data points" ignore {
    val timestamp = new DateTime(0, DateTimeZone.UTC)
    val value = 0.0
    val fixtures = List.tabulate(2000)(n => n + 1) map { n =>
      DataPoint(dpKey, value, timestamp.plusMinutes(n).getMillis)
    }
    val expected = fixtures.size

    lazy val actual = repository.getDataPoints(
      dpKey,
      timestamp.getMillis,
      timestamp.plusMinutes(2000).getMillis,
      desc = true
    ).map(_.size)

    test(fixtures, expected, actual)
  }
}
