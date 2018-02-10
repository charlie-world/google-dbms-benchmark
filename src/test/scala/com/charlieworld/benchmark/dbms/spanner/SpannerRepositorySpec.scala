package com.charlieworld.benchmark.dbms.spanner

import com.charlieworld.benchmark.dbms.DataPoint
import com.charlieworld.benchmark.dbms.tools.Await
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * @author Charlie Lee 
  * @date 2017. 11. 23.
  */
class SpannerRepositorySpec
  extends FlatSpec
    with Matchers
    with SpannerTestKit
    with Await {

  private val keyValue = "test_1"
  private val timestamp = new DateTime(0L, DateTimeZone.UTC)

  private def putDataPoints(dps: Seq[DataPoint]): Unit = {
    repo.putDataPoints(dps).await(12.seconds)
  }

  private def test[T](dps: Seq[DataPoint], expected: T, actual: => Future[T]): Unit = {
    putDataPoints(dps)
    actual.await(12.seconds) shouldBe expected
  }

  "getDataPoints" should "get time-series data points from datastore" ignore {
    val fixtures = Seq.tabulate(1)(n => n + 1) map { x =>
      DataPoint(keyValue, x, timestamp.plusMinutes(x).getMillis)
    }
    val expected = fixtures.reverse
    lazy val actual = repo.getDataPoints(
      keyValue,
      timestamp.getMillis,
      timestamp.plusDays(1).getMillis,
      desc = true
    )

    test(fixtures, expected, actual)
  }
}
