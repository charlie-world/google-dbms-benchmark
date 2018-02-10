package com.charlieworld.benchmark.dbms.simulation

import com.charlieworld.benchmark.dbms.gatling.{read, write}
import com.charlieworld.benchmark.dbms.datastore.DatastoreRepository
import com.charlieworld.benchmark.dbms.gatling.TsdbProtocol
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.spotify.asyncdatastoreclient.{Datastore, DatastoreConfig}
import io.gatling.core.Predef.{Simulation, rampUsersPerSec, scenario}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class DatastoreTsdbSimulation extends Simulation {

  private val datastore: Datastore =
    Datastore.create(
      DatastoreConfig
        .builder()
        .project("test-project")
        .credential(
          GoogleCredential
            .fromStream(getClass.getResourceAsStream("/credentials.json"))
            .createScoped(DatastoreConfig.SCOPES)
        )
        .build()
    )

  private val repository =
    new DatastoreRepository()(ExecutionContext.global, datastore)

  protected def queryRangeInDays: Int = 1

  private val protocol =
    TsdbProtocol(repository)

  private val scn =
    scenario("DatastoreTsdbSimulation")
      .exec(read())
      .exec(write)

  setUp(
    scn.inject(rampUsersPerSec(100).to(500).during(2.minutes))
  ).protocols(protocol)
}
