package com.charlieworld.benchmark.dbms.bigtable

import com.charlieworld.benchmark.dbms.DataPoint
import com.charlieworld.benchmark.dbms.tools.Await
import com.google.cloud.bigtable.hbase.BigtableConfiguration
import org.apache.hadoop.hbase.client.Connection
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by jay on 20/11/2017.
  */
object PutBenchmarkDataPoints extends Await {

  private val projectId: String = "test-project"
  private val instanceId: String = "dbms-benchmark"

  def main(args: Array[String]): Unit = {
    val conf = BigtableConfiguration.configure(projectId, instanceId)

    val bigtableConnection: Connection =
      BigtableConfiguration.connect(conf)

    val repository = new BigTableRepository()(ExecutionContext.global, bigtableConnection)

    def createDataPoints(key: String) =
      for {
        i <- 0 until 10000
        dp = DataPoint(key, 0, new DateTime(0, DateTimeZone.UTC).plusMinutes(i * 5).getMillis)
      } yield dp
    try {
      for {
        i <- 0 until 100
        device <- Seq(s"test_$i")
      } yield {
        repository.putDataPoints(createDataPoints(device)).await(1.minutes)
        println(s"Put 10000 of data point for $device")
      }
    }
    catch {
      case ex: Exception =>
        ex.printStackTrace()
        System.exit(1)
    }
    System.exit(0)
  }
}
