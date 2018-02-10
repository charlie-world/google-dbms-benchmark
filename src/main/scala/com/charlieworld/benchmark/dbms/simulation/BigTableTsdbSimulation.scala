package com.charlieworld.benchmark.dbms.simulation

import akka.actor.ActorSystem
import com.charlieworld.benchmark.dbms.bigtable.BigTableRepository
import com.charlieworld.benchmark.dbms.gatling.TsdbProtocol
import com.charlieworld.benchmark.dbms.tools.Await
import com.charlieworld.benchmark.dbms.warm.WarmupProcess
import com.google.cloud.bigtable.hbase.BigtableConfiguration
import com.charlieworld.benchmark.dbms.gatling._
import io.gatling.core.Predef._
import org.apache.hadoop.hbase.client.Connection

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

final class BigTableTsdbSimulation extends Simulation with Await {

  private val projectId: String = "test-project"
  private val instanceId: String = "dbms-benchmark"
  private val conf = BigtableConfiguration.configure(projectId, instanceId)

  private val bigtableConnection: Connection =
    BigtableConfiguration.connect(conf)

  private val repository =
    new BigTableRepository()(ExecutionContext.global, bigtableConnection)

  private val protocol =
    TsdbProtocol(repository)

  private val scn =
    scenario("BigTableTsdbSimulation")
      .exec(read())
      .exec(write)

  private implicit val actorSystem = ActorSystem(getClass.getSimpleName)

  val warmup = new WarmupProcess()
  val goal = 250.millis
  val lastLatency = warmup.start(goal, 100.millis, 20)(_ => repository.getDataPoints("", 0, 0, desc = true)).await(1.minutes)
  println(s"took $lastLatency, goal was $goal")

  setUp(
    scn.inject(rampUsersPerSec(100).to(500).during(2.minutes))
  ).protocols(protocol)
}
