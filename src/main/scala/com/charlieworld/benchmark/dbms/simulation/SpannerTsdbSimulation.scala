package com.charlieworld.benchmark.dbms.simulation

import com.charlieworld.benchmark.dbms.gatling.TsdbProtocol
import com.charlieworld.benchmark.dbms.spanner.SpannerRepository
import com.charlieworld.benchmark.dbms.gatling._
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.spanner.{DatabaseId, InstanceId, SpannerOptions}
import io.gatling.core.Predef.{Simulation, rampUsersPerSec, scenario}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * @author Charlie Lee 
  * @date 2017. 11. 23.
  */
class SpannerTsdbSimulation extends Simulation {

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

  private val repository =
    new SpannerRepository()(ExecutionContext.global, dbClient)

  protected def queryRangeInDays: Int = 1

  private val protocol =
    TsdbProtocol(repository)

  private val scn =
    scenario("SpannerTsdbSimulation")
      .exec(read())
      .exec(write)

  setUp(
    scn.inject(rampUsersPerSec(100).to(500).during(2.minutes))
  ).protocols(protocol)
}
