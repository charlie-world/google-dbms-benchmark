package com.charlieworld.benchmark.dbms.bigtable

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}

import scala.util.Try

/**
  * Created by jay on 23/11/2017.
  */
trait BigTableKey {

  private val tsFormat =
    DateTimeFormat.forPattern("yyyyMMddHHmmssSSS").withZone(DateTimeZone.UTC)

  def rowKey(key: String, timestamp: Long): String = {
    s"$key#${tsFormat.print(new DateTime(timestamp, DateTimeZone.UTC))}"
  }

  def parseRowKey(rowKey: String): Try[(String, Long)] =
    Try {
      val tokens = rowKey.split("#")
      (tokens(0), tsFormat.parseMillis(tokens(1)))
    }
}
