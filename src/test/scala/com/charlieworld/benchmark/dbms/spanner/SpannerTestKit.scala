package com.charlieworld.benchmark.dbms.spanner

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.{DatabaseId, InstanceId, SpannerOptions}

/**
  * @author Charlie Lee 
  * @date 2017. 11. 23.
  */
trait SpannerTestKit {

  import scala.concurrent.ExecutionContext.Implicits.global

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

  protected val repo = new SpannerRepository()
}
