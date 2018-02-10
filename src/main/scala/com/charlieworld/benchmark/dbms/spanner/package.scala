package com.charlieworld.benchmark.dbms

import DataPoint
import com.google.cloud.spanner.ResultSet

import scala.annotation.tailrec

/**
  * @author Charlie Lee 
  * @date 2017. 11. 23.
  */
package object spanner extends SpannerSupport {

  def spannerTable: String = "test"

  def indexName: String = "KeyAndTimestampIndex"

  @tailrec
  def getAllEntity(z: Vector[DataPoint], results: ResultSet): Vector[DataPoint] = if (results.next) {
    val dataPoint = DataPoint(results.getString(0), results.getDouble(1), results.getTimestamp(2))
    getAllEntity(z :+ dataPoint, results)
  } else {
    z
  }
}
