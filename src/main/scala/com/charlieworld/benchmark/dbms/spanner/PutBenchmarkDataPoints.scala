package com.charlieworld.benchmark.dbms.spanner

import com.charlieworld.benchmark.dbms.DataPoint
import com.charlieworld.benchmark.dbms.tools.Await
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.{DatabaseId, InstanceId, SpannerOptions}
import org.joda.time.{DateTime, DateTimeZone}

import scala.concurrent.duration._

/**
  * @author Charlie Lee 
  * @date 2017. 11. 23.
  */
object PutBenchmarkDataPoints extends Await {
  private val projectId = "test-project"
  private lazy val credentialStream = getClass.getResourceAsStream("/credentials.json")
  private val options = SpannerOptions
    .newBuilder
    .setCredentials(
      GoogleCredentials
        .fromStream(credentialStream)
    )
    .setProjectId(projectId)
    .build()

  private val instance = "dbms-instance"
  private val database = "dbms-test"

  private val instanceId = InstanceId.of(projectId, instance)
  private val dbId = DatabaseId.of(projectId, instance, database)
  implicit val dbClient = options.getService.getDatabaseClient(dbId)

  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val repository = new SpannerRepository()
    def putDataPartitialiy(device: String): Unit = {
      List.tabulate(4)(n => n * 2500) foreach { x =>
        repository.putDataPoints(createDataPoints(device, x)).await(12.seconds)
      }
      println(s"Put 10000 of data point for $device")
    }
    def createDataPoints(key: String, startNumber: Int) =
      for {
        i <- startNumber until startNumber + 2500
        dp = DataPoint(key, 0, new DateTime(0, DateTimeZone.UTC).plusMinutes(i * 5).getMillis)
      } yield dp
    try {
      List.tabulate(100)(n => n + 1) foreach { x =>
        val device = s"test_$x"
        putDataPartitialiy(device)
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
