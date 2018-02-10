package com.charlieworld.benchmark.dbms.datastore

import com.charlieworld.benchmark.dbms.DataPoint
import com.charlieworld.benchmark.dbms.tools.Await
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.spotify.asyncdatastoreclient.{Datastore, DatastoreConfig}
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Created by jay on 20/11/2017.
  */
object PutBenchmarkDataPoints extends Await {

  private val projectId = "test-project"
  private lazy val credentialStream = getClass.getResourceAsStream("/credentials.json")
  private val datastoreConfig = DatastoreConfig.builder()
    .project(projectId)
    .credential(
      GoogleCredential
        .fromStream(credentialStream)
        .createScoped(DatastoreConfig.SCOPES)
    )
    .build()

  def main(args: Array[String]): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    implicit val datastore = Datastore.create(datastoreConfig)
    val repository = new DatastoreRepository()
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
        repository.putDataPoints(createDataPoints(device)).await()
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
