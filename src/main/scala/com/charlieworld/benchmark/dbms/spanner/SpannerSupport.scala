package com.charlieworld.benchmark.dbms.spanner

import com.charlieworld.benchmark.dbms.DataPoint
import com.google.cloud.Timestamp
import com.google.cloud.spanner.Mutation
import org.joda.time.{DateTime, DateTimeZone}

/**
  * @author Charlie Lee 
  * @date 2017. 11. 23.
  */
trait SpannerSupport {

  def keyMaker(key: String, timestamp: Long): String = {
    val datetime = new DateTime(timestamp, DateTimeZone.UTC)
    s"$key#$datetime"
  }

  implicit def dataPointToMutation(dp: DataPoint): Mutation = {
    val timestamp: Timestamp = dp.timestamp
    Mutation
      .newInsertOrUpdateBuilder(spannerTable)
      .set("id")
      .to(keyMaker(dp.key, dp.timestamp))
      .set("key")
      .to(dp.key)
      .set("timestamp")
      .to(timestamp)
      .set("value")
      .to(dp.value)
      .build()
  }

  implicit def dataPointToMutation(dps: Seq[DataPoint]): Seq[Mutation] =
    dps map dataPointToMutation

  implicit def timestampToLong(timestamp: Timestamp): Long =
    timestamp.toSqlTimestamp.getTime

  implicit def longToTimestamp(long: Long): Timestamp =
    Timestamp.parseTimestamp(new DateTime(long, DateTimeZone.UTC).toString)
}
